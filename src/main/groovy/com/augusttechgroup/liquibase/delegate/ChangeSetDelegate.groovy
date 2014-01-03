/*
 * Copyright 2011 Tim Berglund
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.augusttechgroup.liquibase.delegate

import liquibase.change.core.AddColumnChange
import liquibase.change.core.DropAllForeignKeyConstraintsChange
import liquibase.change.core.RenameColumnChange
import liquibase.change.core.DropColumnChange
import liquibase.change.core.AlterSequenceChange
import liquibase.change.core.CreateTableChange
import liquibase.change.core.RenameTableChange
import liquibase.change.core.DropTableChange
import liquibase.change.core.CreateViewChange
import liquibase.change.core.RenameViewChange
import liquibase.change.core.DropViewChange
import liquibase.change.core.MergeColumnChange
import liquibase.change.core.CreateProcedureChange
import liquibase.change.core.AddLookupTableChange
import liquibase.change.core.AddNotNullConstraintChange
import liquibase.change.core.DropNotNullConstraintChange
import liquibase.change.core.AddUniqueConstraintChange
import liquibase.change.core.DropUniqueConstraintChange
import liquibase.change.core.CreateSequenceChange
import liquibase.change.core.DropSequenceChange
import liquibase.change.core.AddAutoIncrementChange
import liquibase.change.core.AddDefaultValueChange
import liquibase.change.core.DropDefaultValueChange
import liquibase.change.core.AddForeignKeyConstraintChange
import liquibase.change.core.DropForeignKeyConstraintChange
import liquibase.change.core.AddPrimaryKeyChange
import liquibase.change.core.DropPrimaryKeyChange
import liquibase.change.core.InsertDataChange
import liquibase.change.core.LoadDataColumnConfig
import liquibase.change.core.LoadDataChange
import liquibase.change.core.LoadUpdateDataChange
import liquibase.change.core.UpdateDataChange
import liquibase.change.core.TagDatabaseChange
import liquibase.change.core.StopChange
import liquibase.change.core.CreateIndexChange
import liquibase.change.core.DropIndexChange
import liquibase.change.core.RawSQLChange
import liquibase.change.core.SQLFileChange
import liquibase.change.core.ExecuteShellCommandChange
import liquibase.change.custom.CustomChangeWrapper
import liquibase.exception.ChangeLogParseException
import liquibase.exception.RollbackImpossibleException
import liquibase.util.ObjectUtil;
import liquibase.change.core.ModifyDataTypeChange
import liquibase.change.core.DeleteDataChange

/**
 * This class is the closure delegate for a ChangeSet.  It processes all the
 * refactoring changes for the ChangeSet.  it basically creates all the changes
 * that need to belong to the ChangeSet, but it doesn't worry too much about
 * validity of the change because Liquibase itself will deal with that.
 * <p>
 * To keep the code simple, we don't worry too much about supporting things
 * that we know to be invalid.  For example, if you try to use a change like
 * <b>addColumn { column(columnName: 'newcolumn') }</b>, you'll get a
 * wonderfully helpful MissingMethodException because of the missing map in
 * the change.  We aren't going to muddy up the code trying to support
 * addColumn changes with no attributes because we know that at least a
 * table name is required.  Similarly, it doesn't make sense to have an
 * addColumn change without at least one column, so we don't deal well with the
 * addColumn change without a closure.
 */
class ChangeSetDelegate {
	def changeSet
	def databaseChangeLog
	def resourceAccessor
	def inRollback

	// ------------------------------------------------------------------------
	// Non refactoring elements.

	void comment(String text) {
		changeSet.comments = DelegateUtil.expandExpressions(text, databaseChangeLog)
	}

	void preConditions(Map params = [:], Closure closure) {
		changeSet.preconditions = PreconditionDelegate.buildPreconditionContainer(databaseChangeLog, changeSet.id, params, closure)
	}

	void validCheckSum(String checksum) {
		changeSet.addValidCheckSum(checksum)
	}

	/**
	 * Process an empty rollback.  This doesn't actually do anything, but empty
	 * rollbacks are allowed by the spec.
	 */
	void rollback() {
		// To support empty rollbacks (allowed by the spec)
	}


	void rollback(String sql) {
		changeSet.addRollBackSQL(DelegateUtil.expandExpressions(sql, databaseChangeLog))
	}

	/**
	 * Process a rollback when the rollback changes are passed in as a closure.
	 * The closure can contain nested refactoring changes or raw sql statements.
	 * I don't know what the XML parser will do, but if the closure contains
	 * both refactorings and ends with SQL, the Groovy DSL parser will append the
	 * SQL to list of rollback changes.
	 * @param closure the closure to evaluate.
	 */
	void rollback(Closure closure) {
		def delegate = new ChangeSetDelegate(changeSet: changeSet,
						databaseChangeLog: databaseChangeLog,
						inRollback: true)
		closure.delegate = delegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		def sql = DelegateUtil.expandExpressions(closure.call(), databaseChangeLog)
		if ( sql ) {
			changeSet.addRollBackSQL(sql)
		}
	}

	/**
	 * Process a rollback when we're doing an attribute based rollback.  The
	 * Groovy DSL parser builds a little bit on the XML parser.  With the XML
	 * parser, if some attributes are given as attributes, but not a changeSetId,
	 * the parser would just skip attribute processing and look for nested tags.
	 * With the Groovy DSL parser, you can't have both a parameter map and a
	 * closure, and all supported attributes are meant to find a change set. What
	 * This means is that if a map was specified, we need to at least have a
	 * valid changeSetId in the map.
	 * @param params
	 */
	void rollback(Map params) {
		// Process map parameters in a way that will alert the user that we've got
		// an invalid key.  This is a bit brute force, but we can clean it up later
		def id = null
		def author = null
		def filePath = null
		params.each { key, value ->
			if ( key == "changeSetId" ) {
				id = DelegateUtil.expandExpressions(value, databaseChangeLog)
			} else if ( key == "id") {
				println "Warning: ChangeSet '${changeSet.id}': the id attribute of a rollback has been deprecated, and will be removed in a future release."
				println "Consider using changeSetId instead."
				id = DelegateUtil.expandExpressions(value, databaseChangeLog)
			} else if ( key == "changeSetAuthor" ) {
				author = DelegateUtil.expandExpressions(value, databaseChangeLog)
			} else if ( key == "author" ) {
				println "Warning: ChangeSet '${changeSet.id}': the author attribute of a rollback has been deprecated, and will be removed in a future release."
				println "Consider using changeSetAuthor instead."
				author = DelegateUtil.expandExpressions(value, databaseChangeLog)
 			} else if ( key == "changeSetPath" ) {
				filePath = DelegateUtil.expandExpressions(value, databaseChangeLog)
			} else {
				throw new ChangeLogParseException("ChangeSet '${changeSet.id}': '${key}' is not a valid rollback attribute.")
			}
		}

		// If we don't at least have an ID, we can't continue.
		if ( id == null ) {
			throw new RollbackImpossibleException("no changeSetId given for rollback in '${changeSet.id}'")
		}

		// If we weren't given a path, use the one from the databaseChangeLog
		if ( filePath == null ) {
			filePath = databaseChangeLog.filePath
		}

		def referencedChangeSet = databaseChangeLog.getChangeSet(filePath, author, id)
		if ( referencedChangeSet ) {
			referencedChangeSet.changes.each { change ->
				changeSet.addRollbackChange(change)
			}
		} else {
			throw new RollbackImpossibleException("Could not find changeSet to use for rollback: ${filePath}:${author}:${id}")
		}
	}

	void modifySql(Map params = [:], Closure closure) {
		if ( closure ) {
			def delegate = new ModifySqlDelegate(params, changeSet)
			closure.delegate = delegate
			closure.resolveStrategy = Closure.DELEGATE_FIRST
			closure.call()

			// No need to expand expressions, the ModifySqlDelegate will do it.
			delegate.sqlVisitors.each {
				changeSet.addSqlVisitor(it)
			}
		}
	}

	void groovyChange(Closure closure) {
		def delegate = new GroovyChangeDelegate(closure, changeSet, resourceAccessor)
		delegate.changeSet = changeSet
		delegate.resourceAccessor = resourceAccessor
		closure.delegate = delegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()
	}

  // -----------------------------------------------------------------------
	// Refactoring changes
	void addColumn(Map params, Closure closure) {
		def change = makeColumnarChangeFromMap('addColumn', AddColumnChange, closure, params, ['catalogName', 'schemaName', 'tableName'])
		addChange(change)
	}


	void renameColumn(Map params) {
		addMapBasedChange('renameColumn', RenameColumnChange, params, ['catalogName', 'schemaName', 'tableName', 'oldColumnName', 'newColumnName', 'columnDataType'])
	}


	void modifyDataType(Map params) {
		addMapBasedChange('modifyDataType', ModifyDataTypeChange, params, ['catalogName', 'schemaName', 'tableName', 'columnName', 'newDataType'])
	}


	void dropColumn(Map params) {
		addMapBasedChange('dropColumn', DropColumnChange, params, ['catalogName', 'schemaName', 'tableName', 'columnName'])
	}


	void alterSequence(Map params) {
		addMapBasedChange('alterSequence', AlterSequenceChange, params, ['sequenceName', 'catalogName', 'schemaName', 'incrementBy', 'minValue', 'maxValue', 'ordered'])
	}


	void createTable(Map params, Closure closure) {
		def change = makeColumnarChangeFromMap('createTable', CreateTableChange, closure, params, ['catalogName', 'schemaName', 'tablespace', 'tableName', 'remarks'])
		addChange(change)
	}


	void renameTable(Map params) {
		addMapBasedChange('renameTable', RenameTableChange, params, ['catalogName', 'schemaName', 'oldTableName', 'newTableName'])
	}


	void dropTable(Map params) {
		addMapBasedChange('dropTable', DropTableChange, params, ['catalogName', 'schemaName', 'tableName', 'cascadeConstraints'])
	}


	void createView(Map params, Closure closure) {
		def change = makeChangeFromMap('createView', CreateViewChange, params, ['catalogName', 'schemaName', 'viewName', 'replaceIfExists', 'selectQuery'])
		change.selectQuery = DelegateUtil.expandExpressions(closure.call(), databaseChangeLog)
		addChange(change)
	}


	void renameView(Map params) {
		addMapBasedChange('renameView', RenameViewChange, params, ['catalogName', 'schemaName', 'oldViewName', 'newViewName'])
	}


	void dropView(Map params) {
		addMapBasedChange('dropView', DropViewChange, params, ['catalogName', 'schemaName', 'viewName'])
	}


	void mergeColumns(Map params) {
		addMapBasedChange('mergeColumns', MergeColumnChange, params, ['catalogName', 'schemaName', 'tableName', 'column1Name', 'column2Name', 'finalColumnName', 'finalColumnType', 'joinString'])
	}

	@Deprecated
	void createStoredProcedure(Map params = [:], Closure closure) {
		println "Warning: ChangeSet '${changeSet.id}': createStoredProcedure has been deprecated, and may be removed in a future release."
		println "Consider using createProcedure instead."

		def change = makeChangeFromMap('createStoredProcedure', CreateProcedureChange, params, ['comments'])
		change.procedureBody = DelegateUtil.expandExpressions(closure.call(), databaseChangeLog)
		addChange(change)
	}


	@Deprecated
	void createStoredProcedure(String storedProc) {
		println "Warning: ChangeSet '${changeSet.id}': createStoredProcedure has been deprecated, and may be removed in a future release."
		println "Consider using createProcedure instead."

		def change = new CreateProcedureChange()
		change.procedureBody = DelegateUtil.expandExpressions(storedProc, databaseChangeLog)
		addChange(change)
	}


	void createProcedure(Map params = [:], Closure closure) {
		def change = makeChangeFromMap('createProcedure', CreateProcedureChange, params, ['comments'])
		change.procedureBody = DelegateUtil.expandExpressions(closure.call(), databaseChangeLog)
		addChange(change)
	}


	void createProcedure(String storedProc) {
		def change = new CreateProcedureChange()
		change.procedureBody = DelegateUtil.expandExpressions(storedProc, databaseChangeLog)
		addChange(change)
	}

	void addLookupTable(Map params) {
		addMapBasedChange('addLookupTable', AddLookupTableChange, params, ['existingTableName', 'existingTableCatalogName', 'existingTableSchemaName', 'existingColumnName', 'newTableName', 'newTableCatalogName', 'newTableSchemaName', 'newColumnName', 'newColumnDataType', 'constraintName'])
	}


	void addNotNullConstraint(Map params) {
		addMapBasedChange('addNotNullConstraint', AddNotNullConstraintChange, params, ['catalogName', 'schemaName', 'tableName', 'columnName', 'defaultNullValue', 'columnDataType'])
	}


	void dropNotNullConstraint(Map params) {
		addMapBasedChange('dropNotNullConstraint', DropNotNullConstraintChange, params, ['catalogName', 'schemaName', 'tableName', 'columnName', 'columnDataType'])
	}


	void addUniqueConstraint(Map params) {
		addMapBasedChange('addUniqueConstraint', AddUniqueConstraintChange, params, ['tablespace', 'catalogName', 'schemaName', 'tableName', 'columnNames', 'constraintName', 'deferrable', 'initiallyDeferred', 'disabled'])
	}


	void dropUniqueConstraint(Map params) {
		addMapBasedChange('dropUniqueConstraint', DropUniqueConstraintChange, params, ['tableName', 'catalogName', 'schemaName', 'constraintName', 'uniqueColumns'])
	}


	void createSequence(Map params) {
		addMapBasedChange('createSequence', CreateSequenceChange, params, ['sequenceName', 'catalogName', 'schemaName', 'incrementBy', 'minValue', 'maxValue', 'ordered', 'startValue', 'cycle'])
	}


	void dropSequence(Map params) {
		addMapBasedChange('dropSequence', DropSequenceChange, params, ['sequenceName', 'catalogName', 'schemaName'])
	}


	void addAutoIncrement(Map params) {
		addMapBasedChange('addAutoIncrement', AddAutoIncrementChange, params, ['tableName', 'catalogName', 'schemaName', 'columnName', 'columnDataType', 'startWith', 'incrementBy'])
	}


	void addDefaultValue(Map params) {
		addMapBasedChange('addDefaultValue', AddDefaultValueChange, params, ['tableName', 'catalogName', 'schemaName', 'columnName', 'columnDataType', 'defaultValue', 'defaultValueNumeric', 'defaultValueBoolean', 'defaultValueDate', 'defaultValueComputed', 'defaultValueSequenceNext'])
	}


	void dropDefaultValue(Map params) {
		addMapBasedChange('dropDefaultValue', DropDefaultValueChange, params, ['tableName', 'catalogName', 'schemaName', 'columnName', 'columnDataType'])
	}

	/**
	 * process an addForeignKeyConstraint change.  This change has 2 deprecated
	 * properties for which we need a warning.
	 * @param params the properties to set on the new changes.
	 */
	void addForeignKeyConstraint(Map params) {
		if ( params['deleteCascade'] != null ) {
			println "Warning: ChangeSet '${changeSet.id}': addForeignKeyConstraint's deleteCascade parameter has been deprecated, and may be removed in a future release."
			println "Consider using \"onDelete='CASCADE'\" instead."
		}
		if ( params['referencesUniqueColumn'] != null ) {
			println "Warning: ChangeSet '${changeSet.id}': addForeignKeyConstraint's referencesUniqueColumn parameter has been deprecated, and may be removed in a future release."
			println "Consider removing it, as Liquibase ignores it anyway."
		}
		addMapBasedChange('addForeignKeyConstraint', AddForeignKeyConstraintChange, params, ['constraintName', 'baseTableName', 'baseTableCatalogName', 'baseTableSchemaName', 'baseColumnNames', 'referencedTableName', 'referencedTableCatalogName', 'referencedTableSchemaName', 'referencedColumnNames', 'deferrable', 'initiallyDeferred', 'onDelete', 'onUpdate', 'deleteCascade', 'referencesUniqueColumn'])
	}


	void dropAllForeignKeyConstraints(Map params) {
		addMapBasedChange('dropAllForeignKeyConstraints', DropAllForeignKeyConstraintsChange, params, ['baseTableName', 'baseTableCatalogName', 'baseTableSchemaName'])
	}


	void dropForeignKeyConstraint(Map params) {
		addMapBasedChange('dropForeignKeyConstraint', DropForeignKeyConstraintChange, params, ['constraintName', 'baseTableName', 'baseTableCatalogName', 'baseTableSchemaName'])
	}


	void addPrimaryKey(Map params) {
		addMapBasedChange('addPrimaryKey', AddPrimaryKeyChange, params, ['tableName', 'catalogName', 'schemaName', 'columnNames', 'constraintName', 'tablespace'])
	}

	void dropPrimaryKey(Map params) {
		addMapBasedChange('dropPrimaryKey', DropPrimaryKeyChange, params, ['tableName', 'catalogName', 'schemaName', 'constraintName'])
	}

	void insert(Map params, Closure closure) {
		def change = makeColumnarChangeFromMap('insert', InsertDataChange, closure, params, ['catalogName', 'schemaName', 'tableName', 'dbms'])
		addChange(change)
	}

	void loadData(Map params, Closure closure) {
		if ( params.file instanceof File ) {
			println "Warning: ChangeSet '${changeSet.id}': using a File object for loadData's 'file' attribute has been deprecated, and may be removed in a future release."
			println "Consider using the path to the file instead."
			params.file = params.file.canonicalPath
		}

		def change = makeLoadDataColumnarChangeFromMap('loadData', LoadDataChange, closure, params, ['catalogName', 'schemaName', 'tableName', 'file', 'encoding', 'separator', 'quotchar'])
		change.resourceAccessor = resourceAccessor
		addChange(change)
	}


	void loadUpdateData(Map params, Closure closure) {
		if ( params.file instanceof File ) {
			println "Warning: ChangeSet '${changeSet.id}': using a File object for loadUpdateData's 'file' attribute has been deprecated, and may be removed in a future release."
			println "Consider using the path to the file instead."
			params.file = params.file.canonicalPath
		}

		def change = makeLoadDataColumnarChangeFromMap('loadUpdateData', LoadUpdateDataChange, closure, params, ['catalogName', 'schemaName', 'tableName', 'file', 'encoding', 'separator', 'quotchar', 'primaryKey'])
		change.resourceAccessor = resourceAccessor
		addChange(change)
	}


	void update(Map params, Closure closure) {
		def change = makeColumnarChangeFromMap('update', UpdateDataChange, closure, params, ['catalogName', 'schemaName', 'tableName'])
		addChange(change)
	}


	void delete(Map params, Closure closure) {
		def change = makeColumnarChangeFromMap('delete', DeleteDataChange, closure, params, ['catalogName', 'schemaName', 'tableName'])
		addChange(change)
	}

	void delete(Map params) {
		addMapBasedChange('delete', DeleteDataChange, params, ['catalogName', 'schemaName', 'tableName'])
	}

	/**
	 * Parse a tagDatabase change.  This version of the method follows the XML
	 * by taking a 'tag' parameter.
	 * @param params params the parameter map
	 */
	void tagDatabase(Map params) {
		addMapBasedChange('tagDatabase', TagDatabaseChange, params, ['tag'])
	}

	/**
	 * Parse a tagDatabase change.  This version of the method is syntactic sugar
	 * that allows {@code tagDatabase 'my-tag-name'} in stead of the usual
	 * parameter based change.
	 * @param tagName the name of the tag to create.
	 */
	void tagDatabase(String tagName) {
		def change = new TagDatabaseChange()
		change.tag = DelegateUtil.expandExpressions(tagName, databaseChangeLog)
		addChange(change)
	}

	/**
	 * Parse a stop change.  This version of the method follows the XML by taking
	 * a 'message' parameter
	 * @param params the parameter map
	 */
	void stop(Map params) {
		addMapBasedChange('stop', StopChange, params, ['message'])
	}

	/**
	 * Parse a stop change.  This version of the method is syntactic sugar that
	 * allows {@code stop 'some message'} in stead of the usual parameter based
	 * change.
	 * @param message the stop message.
	 */
	void stop(String message) {
		def change = new StopChange()
		change.message = DelegateUtil.expandExpressions(message, databaseChangeLog)
		addChange(change)
	}

	void createIndex(Map params, Closure closure) {
		def change = makeColumnarChangeFromMap('createIndex', CreateIndexChange, closure, params, ['catalogName', 'schemaName', 'tableName', 'tablespace', 'indexName', 'unique', 'associatedWith'])
		addChange(change)
	}


	void dropIndex(Map params) {
		addMapBasedChange('dropIndex', DropIndexChange, params, ['tableName', 'catalogName', 'schemaName', 'indexName', 'associatedWith'])
	}


	void sql(Map params = [:], Closure closure) {
		def change = makeChangeFromMap('sql', RawSQLChange, params, ['stripComments', 'splitStatements', 'endDelimiter', 'dbms'])
		def delegate = new CommentDelegate(changeSetId: changeSet.id,
		                                   changeName: 'sql')
		closure.delegate = delegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		// expand expressions because the comment delegate won't...
		change.sql = DelegateUtil.expandExpressions(closure.call(), databaseChangeLog)
		change.comment = (DelegateUtil.expandExpressions(delegate.comment, databaseChangeLog))
		addChange(change)
	}


	void sql(String sql) {
		def change = new RawSQLChange()
		change.sql = DelegateUtil.expandExpressions(sql, databaseChangeLog)
		addChange(change)
	}


	void sqlFile(Map params) {
		def change = makeChangeFromMap('sqlFile', SQLFileChange, params, ['path', 'stripComments', 'splitStatements', 'encoding', 'endDelimiter', 'relativeToChangelogFile', 'dbms'])
		change.resourceAccessor = resourceAccessor
		// Before we add the change, work around the Liquibase bug where sqlFile
		// change sets don't load the SQL until it is too late to calculate
		// checksums properly after a clearChecksum command.  See
		// https://liquibase.jira.com/browse/CORE-1293
		change.finishInitialization()

		addChange(change)
	}

	void customChange(Map params, Closure closure = null) {
		def change = new CustomChangeWrapper()
		change.classLoader = this.class.classLoader
		change.className = DelegateUtil.expandExpressions(params['class'], databaseChangeLog)

		if ( closure ) {
			def delegate = new KeyValueDelegate()
			closure.delegate = delegate
			closure.resolveStrategy = Closure.DELEGATE_FIRST
			closure.call()
			delegate.map.each { key, value ->
				// expandExpressions because the delegate won't
				change.setParam(key, DelegateUtil.expandExpressions(value, databaseChangeLog))
			}
		}

		addChange(change)
	}

	/**
	 * A Groovy-specific extension that allows a closure to be provided,
	 * implementing the change. The closure is passed the instance of
	 * Database.
	 */
	void customChange(Closure closure) {
		//TODO Figure out how to implement closure-based custom changes
		// It's not easy, since the closure would probably need the Database object to be
		// interesting, and that's not available at parse time. Perhaps we could keep this closure
		// around somewhere to run later when the Database is alive.
	}


	void executeCommand(Map params) {
		addMapBasedChange('executeCommand', ExecuteShellCommandChange, params, ['executable', 'os'])
	}


	void executeCommand(Map params, Closure closure) {
		def change = makeChangeFromMap('executeCommand', ExecuteShellCommandChange, params, ['executable', 'os'])
		def delegate = new ArgumentDelegate(changeSetId: changeSet.id,
		                                    changeName: 'executeCommand')
		closure.delegate = delegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()
		delegate.args.each { arg ->
			// expand expressions because the argument delegate won't...
			change.addArg(DelegateUtil.expandExpressions(arg, databaseChangeLog))
		}

		addChange(change)
	}

	/**
	 * Groovy calls methodMissing when it can't find a matching method to call.
	 * We use it to tell the user which changeSet had the invalid element.
	 * @param name the name of the method Groovy wanted to call.
	 * @param args the original arguments to that method.
	 */
	def methodMissing(String name, args) {
		throw new ChangeLogParseException("ChangeSet '${changeSet.id}': '${name}' is not a valid element of a ChangeSet")
	}


	/**
	 * Helper method to make a Liquibase change that takes columns of the type
	 * used by the {@code loadData} change.
	 * @param name the name of the change, used for improved error messages.
	 * @param klass the Liquibase class to create
	 * @param closure the closure containing columns.
	 * @param params a map containing the attributes to set on the newly created
	 *        change
	 * @param paramNames a list of valid parameter names.
	 * @return the newly created change.
	 */
	private def makeLoadDataColumnarChangeFromMap(String name, Class klass, Closure closure, Map params, List paramNames) {
		def change = makeChangeFromMap(name, klass, params, paramNames)

		def columnDelegate = new ColumnDelegate(columnConfigClass: LoadDataColumnConfig,
						                                databaseChangeLog: databaseChangeLog,
		                                        changeSetId: changeSet.id,
		                                        changeName: name)
		closure.delegate = columnDelegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()

		// We use the same columnDelegate for LoadData columns as regular ones, but
		// a where clause is not legal for a load change.  Rather than silently
		// eating a where clause, let the user know.
		if ( columnDelegate.whereClause != null ) {
			throw new ChangeLogParseException("ChangeSet '${changeSet.id}': a where clause is invalid for '${name}' changes.")
		}

		columnDelegate.columns.each { column ->
			change.addColumn(column)
		}

		return change
	}

	/**
	 * Create a Liquibase change for the types of changes that can have a nested
	 * closure of columns and where clauses.
	 * @param name the name of the change to make, used for improved error messages.
	 * @param klass the Liquibase class to create.
	 * @param closure the closure with column information
	 * @param params a map containing attributes of the new change
	 * @param paramNames a list of valid properties for the new change
	 * @return the newly created change
	 */
	private def makeColumnarChangeFromMap(String name, Class klass, Closure closure, Map params, List paramNames) {
		def change = makeChangeFromMap(name, klass, params, paramNames)

		def columnDelegate = new ColumnDelegate(databaseChangeLog: databaseChangeLog,
						                                changeSetId: changeSet.id,
						                                changeName: name)
		closure.delegate = columnDelegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()

		// Try to add the columns to the change.  If we're dealing with something
		// like a "delete" change, we'll get an exception, which we'll rethrow as
		// a parse exception to tell the user that columns are not allowed in that
		// change.
		columnDelegate.columns.each { column ->
			try {
			  change.addColumn(column)
			} catch (MissingMethodException e) {
				throw new ChangeLogParseException("ChangeSet '${changeSet.id}': columns are not allowed in '${name}' changes.")
			}
		}

		if ( columnDelegate.whereClause != null ) {
			try {
				// The columnDelegate DOES take care of expansion.
				ObjectUtil.setProperty(change, 'where', columnDelegate.whereClause)
			} catch (RuntimeException e) {
				throw new ChangeLogParseException("ChangeSet '${changeSet.id}': a where clause is invalid for '${name}' changes.")
			}

		}

		return change
	}

	/**
	 * Create a new Liquibase change and set its properties from the given
	 * map of parameters.
	 * @param klass the type of change to create/
	 * @param sourceMap a map of parameter names and values for the new change
	 * @param paramNames a list of valid parameter names.
	 * @return the newly create change, with the appropriate properties set.
	 * @throws ChangeLogParseException if the source map contains any keys that
	 * are not in the list of valid paramNames.
	 */
	private def makeChangeFromMap(String name, Class klass, Map sourceMap, List paramNames) {
		def change = klass.newInstance()

		// Todo: if we get rid of paramNames, just loop through the source map
		// and set properties.  The else block with the error message goes away.
		sourceMap.each { key, value ->
			if ( paramNames.contains(key) && value != null ) {
				try {
					ObjectUtil.setProperty(change, key, DelegateUtil.expandExpressions(value, databaseChangeLog))
				}
				catch (NumberFormatException ex) {
					change[key] = value.toBigInteger()
				}
			} else {
				throw new ChangeLogParseException("ChangeSet '${changeSet.id}': '${key}' is an invalid property for '${name}' changes.")
			}

		}
		return change
	}

	/**
	 * Helper method used by changes that don't have closures, just attributes
	 * that get set from the parameter map.  This method will add the newly
	 * created change to the current change set.
	 * @param name the name of the change.  Used for improved error messages.
	 * @param klass the Liquibase class to make for the change.
	 * @param sourceMap the map of attributes to set on the Liquibase change.
	 * @param paramNames a list of valid attribute names.
	 */
	private def addMapBasedChange(String name, Class klass, Map sourceMap, List paramNames) {
		addChange(makeChangeFromMap(name, klass, sourceMap, paramNames))
	}

	/**
	 * Helper method to add a change to the current change set.
	 * @param change the change to add
	 * @return the modified change set.
	 */
	private def addChange(change) {
		if ( inRollback ) {
			changeSet.addRollbackChange(change)
		} else {
			changeSet.addChange(change)
		}
		return changeSet
	}
}

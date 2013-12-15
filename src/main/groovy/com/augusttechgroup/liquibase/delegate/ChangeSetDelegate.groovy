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
import liquibase.exception.RollbackImpossibleException
import liquibase.util.ObjectUtil;
import liquibase.change.core.ModifyDataTypeChange
import liquibase.change.core.DeleteDataChange
import org.codehaus.groovy.runtime.typehandling.GroovyCastException


class ChangeSetDelegate {
  def changeSet
  def databaseChangeLog
  def resourceAccessor
  def inRollback


  void comment(String text) {
    changeSet.comments = expandExpressions(text)
  }

  
  void modifySql(Map params = [:], Closure closure){
	if(closure) {
	  def delegate = new ModifySqlDelegate(params, changeSet)
	  closure.delegate = delegate
	  closure.call()
	  
	  delegate.sqlVisitors.each {
	    changeSet.addSqlVisitor(it)
      }
    }
  }
  

  void preConditions(Map params = [:], Closure closure) {
    changeSet.preconditions = PreconditionDelegate.buildPreconditionContainer(databaseChangeLog, params, closure)
  }


  //TODO Verify that this works. Don't fully understand addValidCheckSum() yet...
  void validCheckSum(String checksum) {
    changeSet.addValidCheckSum(checksum)
  }


  void rollback() {
    // To support empty rollbacks (allowed by the spec)
  }

  
  void rollback(String sql) {
    changeSet.addRollBackSQL(expandExpressions(sql))
  }


  void rollback(Closure closure) {
    def delegate = new ChangeSetDelegate(changeSet: changeSet,
                                         databaseChangeLog: databaseChangeLog,
                                         inRollback: true)
    closure.delegate = delegate
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure.call()

    // The delegate should populate the ChangeSet's rollback change list, so there is nothing
    // further to do.
  }


  void rollback(Map params) {
    def referencedChangeSet = databaseChangeLog.getChangeSet(databaseChangeLog.filePath, params.author, params.id)
    if(referencedChangeSet) {
      referencedChangeSet.changes.each { change ->
        changeSet.addRollbackChange(change)
      }
    }
    else {
      throw new RollbackImpossibleException("Could not find changeSet to use for rollback: ${path}:${author}:${id}")
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


  void addColumn(Map params, Closure closure) {
    def change = makeColumnarChangeFromMap(AddColumnChange, closure, params, ['catalogName', 'schemaName', 'tableName'])
    addChange(change)
  }


  void renameColumn(Map params) {
    addMapBasedChange(RenameColumnChange, params, ['catalogName', 'schemaName', 'tableName', 'oldColumnName', 'newColumnName', 'columnDataType'])
  }


  void modifyDataType(Map params) {
    addMapBasedChange(ModifyDataTypeChange, params, ['catalogName', 'schemaName', 'tableName', 'columnName', 'newDataType'])
  }


  void dropColumn(Map params) {
    addMapBasedChange(DropColumnChange, params, ['catalogName', 'schemaName', 'tableName', 'columnName'])
  }


  void alterSequence(Map params) {
    addMapBasedChange(AlterSequenceChange, params, ['sequenceName', 'catalogName', 'schemaName', 'incrementBy', 'minValue', 'maxValue', 'ordered'])
  }


  void createTable(Map params, Closure closure) {
    def change = makeColumnarChangeFromMap(CreateTableChange, closure, params, ['catalogName', 'schemaName', 'tablespace', 'tableName', 'remarks'])
    addChange(change)
  }


  void renameTable(Map params) {
    addMapBasedChange(RenameTableChange, params, ['catalogName', 'schemaName', 'oldTableName', 'newTableName'])
  }


  void dropTable(Map params) {
    addMapBasedChange(DropTableChange, params, ['catalogName', 'schemaName', 'tableName', 'cascadeConstraints'])
  }


 void createView(Map params, Closure closure) {
    def change = makeChangeFromMap(CreateViewChange, params, ['catalogName', 'schemaName', 'viewName', 'replaceIfExists', 'selectQuery'])
    change.selectQuery = expandExpressions(closure.call())
    addChange(change)
  }


  void renameView(Map params) {
    addMapBasedChange(RenameViewChange, params, ['catalogName', 'schemaName', 'oldViewName', 'newViewName'])
  }


  void dropView(Map params) {
    addMapBasedChange(DropViewChange, params, ['catalogName', 'schemaName', 'viewName'])
  }


  void mergeColumns(Map params) {
    addMapBasedChange(MergeColumnChange, params, ['catalogName', 'schemaName', 'tableName', 'column1Name', 'column2Name', 'finalColumnName', 'finalColumnType', 'joinString'])
  }


  void createStoredProcedure(String storedProc) {
    def change = new CreateProcedureChange()
    change.procedureBody = expandExpressions(storedProc)
    addChange(change)
  }


  void addLookupTable(Map params) {
    addMapBasedChange(AddLookupTableChange, params, ['existingTableName', 'existingTableCatalogName', 'existingTableSchemaName', 'existingColumnName', 'newTableName', 'newTableCatalogName', 'newTableSchemaName', 'newColumnName', 'newColumnDataType', 'constraintName'])
  }


  void addNotNullConstraint(Map params) {
    addMapBasedChange(AddNotNullConstraintChange, params, ['catalogName', 'schemaName', 'tableName', 'columnName', 'defaultNullValue', 'columnDataType'])
  }


  void dropNotNullConstraint(Map params) {
    addMapBasedChange(DropNotNullConstraintChange, params, ['catalogName', 'schemaName', 'tableName', 'columnName', 'columnDataType'])
  }


  void addUniqueConstraint(Map params) {
    addMapBasedChange(AddUniqueConstraintChange, params, ['tablespace', 'catalogName', 'schemaName', 'tableName', 'columnNames', 'constraintName', 'deferrable', 'initiallyDeferred', 'disabled'])
  }


  void dropUniqueConstraint(Map params) {
    addMapBasedChange(DropUniqueConstraintChange, params, ['tableName', 'catalogName', 'schemaName', 'constraintName', 'uniqueColumns'])
  }


  void createSequence(Map params) {
    addMapBasedChange(CreateSequenceChange, params, ['sequenceName', 'catalogName', 'schemaName', 'incrementBy', 'minValue', 'maxValue', 'ordered', 'startValue', 'cycle'])
  }


  void dropSequence(Map params) {
    addMapBasedChange(DropSequenceChange, params, ['sequenceName', 'catalogName', 'schemaName'])
  }


  void addAutoIncrement(Map params) {
    addMapBasedChange(AddAutoIncrementChange, params, ['tableName', 'catalogName', 'schemaName', 'columnName', 'columnDataType', 'startWith', 'incrementBy'])
  }


  void addDefaultValue(Map params) {
    addMapBasedChange(AddDefaultValueChange, params, ['tableName', 'catalogName', 'schemaName', 'columnName', 'columnDataType', 'defaultValue', 'defaultValueNumeric', 'defaultValueBoolean', 'defaultValueDate', 'defaultValueComputed', 'defaultValueSequenceNext'])
  }


  void dropDefaultValue(Map params) {
    addMapBasedChange(DropDefaultValueChange, params, ['tableName', 'catalogName', 'schemaName', 'columnName', 'columnDataType'])
  }


  void addForeignKeyConstraint(Map params) {
    addMapBasedChange(AddForeignKeyConstraintChange, params, ['constraintName', 'baseTableName', 'baseTableCatalogName', 'baseTableSchemaName', 'baseColumnNames', 'referencedTableName', 'referencedTableCatalogName', 'referencedTableSchemaName', 'referencedColumnNames', 'deferrable', 'initiallyDeferred', 'onDelete', 'onUpdate', 'deleteCascade', 'referencesUniqueColumn'])
  }


  void dropAllForeignKeyConstraintsChange(Map params) {
    addMapBasedChange(DropAllForeignKeyConstraintsChange, params, ['baseTableName', 'baseTableCatalogName', 'baseTableSchemaName'])
  }


  void dropForeignKeyConstraint(Map params) {
		addMapBasedChange(DropForeignKeyConstraintChange, params, ['constraintName', 'baseTableName', 'baseTableCatalogName', 'baseTableSchemaName'])
	}


  void addPrimaryKey(Map params) {
    addMapBasedChange(AddPrimaryKeyChange, params, ['tableName', 'catalogName', 'schemaName', 'columnNames', 'constraintName', 'tablespace'])
  }

  void dropPrimaryKey(Map params) {
    addMapBasedChange(DropPrimaryKeyChange, params, ['tableName', 'catalogName', 'schemaName', 'constraintName'])
  }

  void insert(Map params, Closure closure) {
    def change = makeColumnarChangeFromMap(InsertDataChange, closure, params, ['catalogName', 'schemaName', 'tableName', 'dbms'])
    addChange(change)
  }

  void loadData(Map params, Closure closure) {
    if(params.file instanceof File) {
      params.file = params.file.canonicalPath
    }

    def change = makeLoadDataColumnarChangeFromMap(LoadDataChange, closure, params, ['catalogName', 'schemaName', 'tableName', 'file', 'encoding', 'separator', 'quotchar'])
    change.resourceAccessor = resourceAccessor
    addChange(change)
  }


  void loadUpdateData(Map params, Closure closure) {
    if(params.file instanceof File) {
      params.file = params.file.canonicalPath
    }

    def change = makeLoadDataColumnarChangeFromMap(LoadUpdateDataChange, closure, params, ['catalogName', 'schemaName', 'tableName', 'file', 'encoding', 'separator', 'quotchar', 'primaryKey'])
    addChange(change)
  }


  void update(Map params, Closure closure) {
    def change = makeColumnarChangeFromMap(UpdateDataChange, closure, params, ['catalogName', 'schemaName', 'tableName', 'where'])
    addChange(change)
  }


  void delete(Map params) {
    addMapBasedChange(DeleteDataChange, params, ['catalogName', 'schemaName', 'tableName', 'where'])
  }


  void tagDatabase(Map params) {
    addMapBasedChange(TagDatabaseChange, params, ['tag'])
  }


  void stop(String message) {
    def change = new StopChange()
    change.message = message
    addChange(change)
  }


  void createIndex(Map params, Closure closure) {
    def change = makeColumnarChangeFromMap(CreateIndexChange, closure, params, ['catalogName', 'schemaName', 'tableName', 'tablespace', 'indexName', 'unique'])
    addChange(change)
  }


  void dropIndex(Map params) {
    addMapBasedChange(DropIndexChange, params, ['tableName', 'catalogName', 'schemaName', 'indexName'])
  }


  void sql(Map params = [:], Closure closure) {
    def change = makeChangeFromMap(RawSQLChange, params, ['stripComments', 'splitStatements', 'endDelimiter', 'dbms'])
    change.sql = expandExpressions(closure.call())
    addChange(change)
  }


  void sql(String sql) {
    def change = new RawSQLChange()
    change.sql = expandExpressions(sql)
    addChange(change)
  }


  void sqlFile(Map params) {
    def change = makeChangeFromMap(SQLFileChange, params, ['path', 'stripComments', 'splitStatements', 'encoding', 'endDelimiter', 'relativeToChangelogFile', 'dbms'])
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
    change.className = params['class']

    if(closure) {
      def delegate = new KeyValueDelegate()
      closure.delegate = delegate
      closure.call()
      delegate.map.each { key, value ->
        change.setParam(key, expandExpressions(value))
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
    addMapBasedChange(ExecuteShellCommandChange, params, ['executable', 'os'])
  }


  void executeCommand(Map params, Closure closure) {
    def change = makeChangeFromMap(ExecuteShellCommandChange, params, ['executable', 'os'])
    def delegate = new ArgumentDelegate()
    closure.delegate = delegate
    closure.call()
    delegate.args.each { arg ->
      change.addArg(expandExpressions(arg))
    }

    addChange(change)
  }


  private def makeLoadDataColumnarChangeFromMap(Class klass, Closure closure, Map params, List paramNames) {
    def change = makeChangeFromMap(klass, params, paramNames)

    def columnDelegate = new ColumnDelegate(columnConfigClass: LoadDataColumnConfig, databaseChangeLog: databaseChangeLog)
    closure.delegate = columnDelegate
    closure.call()

    columnDelegate.columns.each { column ->
      change.addColumn(column)
    }

    return change
  }


  private def makeColumnarChangeFromMap(Class klass, Closure closure, Map params, List paramNames) {
    def change = makeChangeFromMap(klass, params, paramNames)

    def columnDelegate = new ColumnDelegate(databaseChangeLog: databaseChangeLog)
    closure.delegate = columnDelegate
    closure.call()

    columnDelegate.columns.each { column ->
      change.addColumn(column)
    }

    return change
  }


  private def makeChangeFromMap(Class klass, Map sourceMap, List paramNames) {
    def change = klass.newInstance()

    paramNames.each { name ->
      if(sourceMap[name] != null) {
        try {
          ObjectUtil.setProperty(change, name, expandExpressions(sourceMap[name]))
        }
        catch(NumberFormatException ex) {
          change[name] = sourceMap[name].toBigInteger()
        }
      }
    }
    return change
  }


  private def addMapBasedChange(Class klass, Map sourceMap, List paramNames) {
    addChange(makeChangeFromMap(klass, sourceMap, paramNames))
  }


  private def addChange(change) {
    if(inRollback) {
      changeSet.addRollbackChange(change)
    }
    else {
      changeSet.addChange(change)
    }
    return changeSet
  }
  
  private def expandExpressions(expression) {
    databaseChangeLog.changeLogParameters.expandExpressions(expression.toString())
  }
}

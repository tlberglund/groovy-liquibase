//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

package com.augusttechgroup.liquibase.delegate

import liquibase.change.core.AddColumnChange
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
import liquibase.change.core.ModifyDataTypeChange
import liquibase.change.core.DeleteDataChange


class ChangeSetDelegate {
  def changeSet
  def databaseChangeLog
  def resourceAccessor
  def inRollback


  void comment(String text) {
    changeSet.comments = text
  }


  void preConditions(Map params = [:], Closure closure) {
    changeSet.preconditions = PreconditionDelegate.buildPreconditionContainer(params, closure)
  }


  //TODO Verify that this works. Don't fully understand addValidCheckSum() yet...
  void validCheckSum(String checksum) {
    changeSet.addValidCheckSum(checksum)
  }


  void rollback() {
    // To support empty rollbacks (allowed by the spec)
  }

  
  void rollback(String sql) {
    changeSet.addRollBackSQL(sql)
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
    closure.resolveStrategy = Closure.DELEGATE_ONLY
    closure.call()
  }


  void addColumn(Map params, Closure closure) {
    def change = makeColumnarChangeFromMap(AddColumnChange, closure, params, ['schemaName', 'tableName'])
    addChange(change)
  }


  void renameColumn(Map params) {
    addMapBasedChange(RenameColumnChange, params, ['schemaName', 'tableName', 'oldColumnName', 'newColumnName', 'columnDataType'])
  }


  void modifyDataType(Map params) {
    addMapBasedChange(ModifyDataTypeChange, params, ['schemaName', 'tableName', 'columnName', 'newDataType'])
  }


  void dropColumn(Map params) {
    addMapBasedChange(DropColumnChange, params, ['schemaName', 'tableName', 'columnName', 'cascadeConstraints'])
  }


  void alterSequence(Map params) {
    addMapBasedChange(AlterSequenceChange, params, ['sequenceName', 'incrementBy'])
  }


  void createTable(Map params, Closure closure) {
    def change = makeColumnarChangeFromMap(CreateTableChange, closure, params, ['schemaName', 'tablespace', 'tableName', 'remarks'])
    addChange(change)
  }


  void renameTable(Map params) {
    addMapBasedChange(RenameTableChange, params, ['schemaName', 'oldTableName', 'newTableName'])
  }


  void dropTable(Map params) {
    addMapBasedChange(DropTableChange, params, ['schemaName', 'tableName'])
  }


  void createView(Map params, Closure closure) {
    def change = makeChangeFromMap(CreateViewChange, params, ['schemaName', 'viewName', 'replaceIfExists'])
    change.selectQuery = closure.call()
    addChange(change)
  }


  void renameView(Map params) {
    addMapBasedChange(RenameViewChange, params, ['schemaName', 'oldViewName', 'newViewName'])
  }


  void dropView(Map params) {
    addMapBasedChange(DropViewChange, params, ['schemaName', 'viewName'])
  }


  void mergeColumns(Map params) {
    addMapBasedChange(MergeColumnChange, params, ['schemaName', 'tableName', 'column1Name', 'column2Name', 'finalColumnName', 'finalColumnType', 'joinString'])
  }


  void createStoredProcedure(String storedProc) {
    def change = new CreateProcedureChange()
    change.procedureBody = storedProc
    addChange(change)
  }


  void addLookupTable(Map params) {
    addMapBasedChange(AddLookupTableChange, params, ['existingTableName', 'existingTableSchemaName', 'existingColumnName', 'newTableName', 'newTableSchemaName', 'newColumnName', 'newColumnDataType', 'constraintName'])
  }


  void addNotNullConstraint(Map params) {
    addMapBasedChange(AddNotNullConstraintChange, params, ['schemaName', 'tableName', 'columnName', 'defaultNullValue', 'columnDataType'])
  }


  void dropNotNullConstraint(Map params) {
    addMapBasedChange(DropNotNullConstraintChange, params, ['schemaName', 'tableName', 'columnName', 'columnDataType'])
  }


  void addUniqueConstraint(Map params) {
    addMapBasedChange(AddUniqueConstraintChange, params, ['tablespace', 'schemaName', 'tableName', 'columnNames', 'constraintName'])
  }


  void dropUniqueConstraint(Map params) {
    addMapBasedChange(DropUniqueConstraintChange, params, ['tableName', 'schemaName', 'constraintName'])
  }


  void createSequence(Map params) {
    addMapBasedChange(CreateSequenceChange, params, ['sequenceName', 'schemaName', 'incrementBy', 'minValue', 'maxValue', 'ordered', 'startValue'])
  }


  void dropSequence(Map params) {
    addMapBasedChange(DropSequenceChange, params, ['sequenceName'])
  }


  void addAutoIncrement(Map params) {
    addMapBasedChange(AddAutoIncrementChange, params, ['tableName', 'columnName', 'columnDataType'])
  }


  void addDefaultValue(Map params) {
    addMapBasedChange(AddDefaultValueChange, params, ['tableName', 'schemaName', 'columnName', 'defaultValue', 'defaultValueNumeric', 'defaultValueBoolean', 'defaultValueDate'])
  }


  void dropDefaultValue(Map params) {
    addMapBasedChange(DropDefaultValueChange, params, ['tableName', 'schemaName', 'columnName'])
  }


  void addForeignKeyConstraint(Map params) {
    addMapBasedChange(AddForeignKeyConstraintChange, params, ['constraintName', 'baseTableName', 'baseTableSchemaName', 'baseColumnNames', 'referencedTableName', 'referencedTableSchemaName', 'referencedColumnNames', 'deferrable', 'initiallyDeferred', 'deleteCascade', 'onDelete', 'onUpdate'])
  }


  void dropForeignKeyConstraint(Map params) {
    addMapBasedChange(DropForeignKeyConstraintChange, params, ['constraintName', 'baseTableName', 'baseTableSchemaName'])
  }


  void addPrimaryKey(Map params) {
    addMapBasedChange(AddPrimaryKeyChange, params, ['tableName', 'schemaName', 'columnNames', 'constraintName', 'tablespace'])
  }


  void dropPrimaryKey(Map params) {
    addMapBasedChange(DropPrimaryKeyChange, params, ['tableName', 'schemaName', 'constraintName'])
  }


  void insert(Map params, Closure closure) {
    def change = makeColumnarChangeFromMap(InsertDataChange, closure, params, ['schemaName', 'tableName'])
    addChange(change)
  }


  void loadData(Map params, Closure closure) {
    if(params.file instanceof File) {
      params.file = params.file.canonicalPath
    }

    def change = makeLoadDataColumnarChangeFromMap(LoadDataChange, closure, params, ['schemaName', 'tableName', 'file', 'encoding'])
    change.resourceAccessor = resourceAccessor
    addChange(change)
  }


  void loadUpdateData(Map params, Closure closure) {
    if(params.file instanceof File) {
      params.file = params.file.canonicalPath
    }

    def change = makeLoadDataColumnarChangeFromMap(LoadUpdateDataChange, closure, params, ['schemaName', 'tableName', 'file', 'encoding', 'primaryKey'])
    addChange(change)
  }


  void update(Map params, Closure closure) {
    def change = makeColumnarChangeFromMap(UpdateDataChange, closure, params, ['schemaName', 'tableName'])
    addChange(change)
  }


  void delete(Map params, Closure closure) {
    def change = makeColumnarChangeFromMap(DeleteDataChange, closure, params, ['schemaName', 'tableName'])
    addChange(change)
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
    def change = makeColumnarChangeFromMap(CreateIndexChange, closure, params, ['schemaName', 'tableName', 'tablespace', 'indexName', 'unique'])
    addChange(change)
  }


  void dropIndex(Map params) {
    addMapBasedChange(DropIndexChange, params, ['tableName', 'indexName'])
  }


  void sql(Map params = [:], Closure closure) {
    def change = makeChangeFromMap(RawSQLChange, params, ['stripComments', 'splitStatements', 'endDelimiter'])
    change.sql = closure.call()
    addChange(change)
  }


  void sql(String sql) {
    def change = new RawSQLChange()
    change.sql = sql
    addChange(change)
  }


  void sqlFile(Map params) {
    def change = makeChangeFromMap(SQLFileChange, params, ['path', 'stripComments', 'splitStatements', 'encoding', 'endDelimiter', 'relativeToChangelogFile'])
    change.resourceAccessor = resourceAccessor
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
        change.setParam(key, value.toString())
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
      change.addArg(arg)
    }

    addChange(change)
  }


  private def makeLoadDataColumnarChangeFromMap(Class klass, Closure closure, Map params, List paramNames) {
    def change = makeChangeFromMap(klass, params, paramNames)

    def columnDelegate = new ColumnDelegate(columnConfigClass: LoadDataColumnConfig)
    closure.delegate = columnDelegate
    closure.call()

    columnDelegate.columns.each { column ->
      change.addColumn(column)
    }

    return change
  }


  private def makeColumnarChangeFromMap(Class klass, Closure closure, Map params, List paramNames) {
    def change = makeChangeFromMap(klass, params, paramNames)

    def columnDelegate = new ColumnDelegate()
    closure.delegate = columnDelegate
    closure.call()

    columnDelegate.columns.each { column ->
      change.addColumn(column)
    }

    // It is a bit sloppy to do this here from a coherence standpoint, but where clauses mostly
    // only get used when we are dealing with columns
    if(columnDelegate.whereClause != null) {
      change.whereClause = columnDelegate.whereClause
    }

    return change
  }


  private def makeChangeFromMap(Class klass, Map sourceMap, List paramNames) {
    def change = klass.newInstance()
    paramNames.each { name ->
      if(sourceMap[name] != null) {
        change[name] = sourceMap[name]
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

}

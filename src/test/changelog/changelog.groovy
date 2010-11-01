databaseChangeLog(logicalFilePath: '') {
  
  preConditions {
    and {
      dbms(type: 'mysql')
      runningAs(username: 'root')
      or {
        changeSetExecuted(id: '', author: '', changeLogFile: '')
        columnExists(schemaName: '', tableName: '', columnName: '')
        tableExists(schemaName: '', tableName: '')
        viewExists(schemaName: '', viewName: '')
        foreignKeyConstraintExists(schemaName: '', foreignKeyName: '')
        indexExists(schemaName: '', indexName: '')
        sequenceExists(schemaName: '', sequenceName: '')
        primaryKeyExists(schemaName: '', primaryKeyName: '', tableName: '')
        sqlCheck(expectedResult: '') {
          "SELECT COUNT(1) FROM monkey WHERE status='angry'"
        }
        customPrecondition(className: '') {
          tableName('our_table')
          count(42)
        }
      }
    }
  }

  include(file: '', relative: true)
  
  //TODO figure out what properties are all about
  clobType = 0

  changeSet(id: '', author: '', dbms: '', runAlways: true, runOnChange: false, context: '', runInTransaction: true, failOnError: false) {
    // Comments supported through Groovy
    comment "Liquibase can be aware of this comment"

    preConditions {
      // just like changelog preconditions
    }
    
    validCheckSum 'd0763edaa9d9bd2a9516280e9044d885'
    
    // If rollback takes a string, it's just the SQL to execute
    rollback "DROP TABLE monkey_table"
    rollback """
      UPDATE monkey_table SET emotion='angry' WHERE status='PENDING';
      ALTER TABLE monkey_table DROP COLUMN angry;
    """
    
    // If rollback takes a closure, it's more Liquibase builder (a changeSet?)
    rollback {
      dropTable(tableName: 'monkey_table')
    }
    
    // If rollback takes a map, it identifies the changeset to re-run to do the rollback (this file assumed)
    rollback(changeSetId: '', changeSetAuthor: '')
    
  }
  
  
  changeSet(id: 'add-column', author: 'tlberglund') {
    addColumn(tableName: '', schemaName: '') {
      column(name: '', type: '', value: '', defaultValue: '', autoIncrement: false, remarks: '') {
        
        // Seems like you should have two ways of representing constraints
        
        // Pass a closure
        constraints {
          nullable(false)
          primaryKey(true)
          unique(true)
          uniqueConstraintName('make_it_unique_yo')
          foreignKeyName('key_to_monkey')
          references('monkey_table')
          deleteCascade(true)
          deferrable(true)
          initiallyDeferred(false)
        }
        
        // Or pass a map
        // Can put all constraints in one call, or split them up as shown
        constraints(nullable: false, primaryKey: true)
        constraints(unique: true, uniqueConstraintName: 'make_it_unique_yo')
        constraints(foreignKeyName: 'key_to_monkey', references: 'monkey_table')
        constraints(deleteCascase: true)
        constraints(deferrable: true, initiallyDeferred: false)
      }
      

      // Examples of other value types (only one would apply inside addColumn)
      column(name: '', type: '', valueNumeric: '', defaultValueNumeric: '')
      column(name: '', type: '', valueBoolean: '', defaultValueBoolean: '')
      column(name: '', type: '', valueDate: '', defaultValueDate: '')
    }
  }
  
  
  changeSet(id: 'rename-column', author: 'tlberglund') {
    renameColumn(schemaName: '', tableName: '', oldColumnName: '', newColumnName: '', columnDataType: '')
  }
  
  
  changeSet(id: 'modify-column', author: 'tlberglund') {
    modifyColumn(schemaName: '', tableName: '') {
      column() { }
    }
  }
  
  
  changeSet(id: 'drop-column', author: 'tlberglund') {
    dropColumn(schemaName: '', tableName: '', columnName: '')
  }
  
  
  changeSet(id: 'alter-sequence', author: 'tlberglund') {
    alterSequence(sequenceName: '', incrementBy: '')
  }
  
  
  changeSet(id: 'create-table', author: 'tlberglund') {
    createTable(schemaName: '', tablespace: '', tableName: '', remarks: '') {
      column() {}
      column() {}
      column() {}
      column() {}
    }
  }
  
  
  changeSet(id: 'rename-table', author: 'tlberglund') {
    renameTable(schemaName: '', oldTableName: '', newTableName: '')
  }
  
  
  changeSet(id: 'drop-table', author: 'tlberglund') {
    dropTable(schemaName: '', tableName: '')
  }
  
  
  changeSet(id: 'create-view', author: 'tlberglund') {
    createView(schemaName: '', viewName: '', replaceIfExists: true) {
      "SELECT id, emotion FROM monkey"
    }
  }
  
  
  changeSet(id: 'rename-view', author: 'tlberglund') {
    renameView(schemaName: '', oldViewName: '', newViewName: '')
  }
  
  
  changeSet(id: 'drop-view', author: 'tlberglund') {
    dropView(schemaName: '', viewName: '')
  }
  
  
  changeSet(id: 'merge-columns', author: 'tlberglund') {
    mergeColumns(schemaName: '', tableName: '', column1Name: '', column2Name: '', finalColumnName: '', finalColumnType: '', joinString: ' ')
  }
  
  
  changeSet(id: 'create-stored-proc', author: 'tlberglund') {
    createStoredProcedure """
      CREATE OR REPLACE PROCEDURE testMonkey
      IS
      BEGIN
       -- do something with the monkey
      END;
    """
  }
  
  
  changeSet(id: 'add-lookup-table', author: 'tlberglund') {
    addLookupTable(existingTableName: '', existingColumnName: '', newTableName: '', newColumnName: '', constraintName: '')
  }
  
  
  changeSet(id: 'add-not-null-constraint', author: 'tlberglund') {
    addNotNullConstraint(tableName: '', columnName: '', defaultNullValue: '')
  }
  
  
  changeSet(id: 'drop-not-null-constraint', author: 'tlberglund') {
    dropNotNullConstraint(schemaName: '', tableName: '', columnName: '', columnDataType: '')
  }
  
  
  changeSet(id: 'add-unique-constraint', author: 'tlberglund') {
    addUniqueConstraint(tableName: '', columnNames: '', constraintName: '')
  }
  
  
  changeSet(id: 'drop-unique-constraint', author: 'tlberglund') {
    dropUniqueConstraint(schemaName: '', tableName: '', constraintName: '')
  }
  
  
  changeSet(id: 'create-sequence', author: 'tlberglund') {
    createSequence(sequenceName: '', schemaName: '' incrementBy: '', minValue: '', maxValue: '', ordered: true, startValue: '')
  }
  
  
  changeSet(id: 'drop-sequence', author: 'tlberglund') {
    dropSequence(sequenceName: '')
  }
  
  
  changeSet(id: 'add-auto-increment', author: 'tlberglund') {
    addAutoIncrement(schemaName: '', tableName: '', columnName: '', columnDataType: '')
  }
  
  
  changeSet(id: 'add-default-value', author: 'tlberglund') {
    addDefaultValue(schemaName: '', tableName: '', columnName: '', defaultValue: '')
    addDefaultValue(schemaName: '', tableName: '', columnName: '', defaultValueNumeric: '')
    addDefaultValue(schemaName: '', tableName: '', columnName: '', defaultValueBoolean: '')
    addDefaultValue(schemaName: '', tableName: '', columnName: '', defaultValueDate: '')
  }
  
  
  changeSet(id: 'drop-default-value', author: 'tlberglund') {
    dropDefaultValue(schemaName: '', tableName: '', columnName: '')
  }
  
  
  changeSet(id: 'add-foreign-key-constraint', author: 'tlberglund') {
    addForeignKeyConstraint(constraintName: '', 
                            baseTableName: '', baseTableSchemaName: '', baseColumnNames: '',
                            referencedTableName: '', referencedTableSchemaName: '', referencedColumnNames: '',
                            deferrable: true,
                            initiallyDeferred: false,
                            deleteCascase: true,
                            onDelete: 'CASCADE|SET NULL|SET DEFAULT|RESTRICT|NO ACTION',
                            onUpdate: 'CASCADE|SET NULL|SET DEFAULT|RESTRICT|NO ACTION')
  }
  
  
  changeSet(id: 'drop-foreign-key', author: 'tlberglund') {
    dropForeignKeyConstraint(constraintName: '', baseTableName: '', baseTableSchemaName: '')
  }
  
  
  changeSet(id: 'add-primary-key', author: 'tlberglund') {
    addPrimaryKey(schemaName: '', tablespace: '', tableName: '', columnNames: '', constraintName: '')
  }
  
  
  changeSet(id: 'drop-primary-key', author: 'tlberglund') {
    dropPrimaryKey(schemaName: '', tableName: '', constraintName: '')
  }
  
  
  changeSet(id: 'insert-data', author: 'tlberglund') {
    insert(schemaName: '', tableName: '') {
      column(name: '', value: '')
      column(name: '', valueNumeric: '')
      column(name: '', valueDate: '')
      column(name: '', valueBoolean: '')
    }
  }
  
  
  changeSet(id: 'load-data', author: 'tlberglund') {
    loadData(schemaName: '', tableName: '', file: '', encoding: 'UTF8|etc') {
      column(name: '', index: 2, type: 'NUMERIC')
      column(name: '', index: 3, type: 'BOOLEAN')
      column(name: '', header: 'shipDate', type: 'DATE')
      column(name: '', index: 5, type: 'STRING')
    }
  }
  
  
  changeSet(id: 'load-update-data', author: 'tlberglund') {
    loadUpdateData(schemaName: '', tableName: '', primaryKey: '', file: '', encoding: '') {
      column(name: '', index: 2, type: 'NUMERIC')
      column(name: '', index: 3, type: 'BOOLEAN')
      column(name: '', header: 'shipDate', type: 'DATE')
      column(name: '', index: 5, type: 'STRING')
    }
  }
  
  
  changeSet(id: 'update', author: 'tlberglund') {
    update(schemaName: '', tableName: '') {
      column(name: '', value: '')
      column(name: '', valueNumeric: '')
      column(name: '', valueDate: '')
      column(name: '', valueBoolean: '')
      where "species='monkey' AND status='angry'"
    }
  }
  
  
  changeSet(id: 'delete-data', author: 'tlberglund') {
    delete(schemaName: '', tableName: '') {
        where "id=39" // optional
    }
  }
  
  
  changeSet(id: 'tag', author: 'tlberglund') {
    tagDatabase(tag: 'monkey')
  }
  
  
  changeSet(id: 'stop', author: 'tlberglund') {
    stop('Migration stopped because something bad went down')
  }
  
  
  changeSet(id: 'create-index', author: 'tlberglund') {
    createIndex(schemaName: '', tablespace: '', tableName: '', indexName: '', unique: true) {
      column(name: '')
      column(name: '')
      column(name: '')
    }
  }
  
  
  changeSet(id: 'drop-index', author: 'tlberglund') {
    dropIndex(tableName: '', indexName: '')
  }
  
  
  changeSet(id: 'custom-sql', author: 'tlberglund') {
    sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
      "INSERT INTO ANIMALS (id, species, status) VALUES (1, 'monkey', 'angry')"
    }
  }
  
  
  changeSet(id: 'sql-file', author: 'tlberglund') {
    sqlFile(path: '', stripComments: true, splitStatements: '', encoding: '', endDelimiter: '')
  }
  
  
  changeSet(id: 'custom-refactoring', author: 'tlberglund') {
    customChange(class: 'com.augusttechgroup.liquibase.MonkeyRefactoring') {
      tableName('animal')
      species('monkey')
      status('angry')
    }
  }
  
  
  changeSet(id: 'shell-command', author: 'tlberglund') {
    executeCommand(executable: '') {
      arg('--monkey')
      arg('--skip:1')
    }
  }
    
}
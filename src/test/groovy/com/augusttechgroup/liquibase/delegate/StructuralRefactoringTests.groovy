//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the GNU Lesser General Public License v2.1
//

package com.augusttechgroup.liquibase.delegate

import liquibase.change.core.AddColumnChange

import org.junit.Test
import static org.junit.Assert.*

import liquibase.change.ColumnConfig
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


class StructuralRefactoringTests
  extends ChangeSetTests
{

  @Test void addMinimalColumnWithoutConstraints() {
    buildChangeSet {
      addColumn(tableName: 'animal') {
        column(name: 'monkey_status', type: 'varchar(98)')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddColumnChange
    assertNull changes[0].schemaName
    assertEquals 'animal', changes[0].tableName
    def columns = changes[0].columns
    assertNotNull columns
    assertEquals 1, columns.size()
  }


  @Test void addColumnIncludingTablespace() {
    buildChangeSet {
      addColumn(schemaName: 'oracle_use_only', tableName: 'animal') {
        column(name: 'monkey_status', type: 'varchar(98)')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddColumnChange
    assertEquals 'oracle_use_only', changes[0].schemaName
    assertEquals 'animal', changes[0].tableName
    def columns = changes[0].columns
    assertNotNull columns
    assertEquals 1, columns.size()
  }


  @Test
  void renameColumn() {
    buildChangeSet {
      renameColumn(tableName: 'monkey', oldColumnName: 'fail', newColumnName: 'win', columnDataType: 'varchar(9001)')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof RenameColumnChange
    assertNull changes[0].schemaName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'fail', changes[0].oldColumnName
    assertEquals 'win', changes[0].newColumnName
    assertEquals 'varchar(9001)', changes[0].columnDataType
  }


  @Test
  void dropColumn() {
    buildChangeSet {
      dropColumn(schemaName: 'schema', tableName: 'monkey', columnName: 'emotion')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropColumnChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'emotion', changes[0].columnName
  }


  @Test
  void alterSequence() {
    buildChangeSet {
      alterSequence(sequenceName: 'seq', incrementBy: 314)
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AlterSequenceChange
    assertEquals 'seq', changes[0].sequenceName
    assertEquals 314G, changes[0].incrementBy
  }


  @Test
  void createTable() {
    buildChangeSet {
      createTable(schemaName: 'schema', tablespace: 'oracle_tablespace', tableName: 'monkey', remarks: 'angry') {
        column(name: 'status', type: 'varchar(100)')
        column(name: 'id', type: 'int')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof CreateTableChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'oracle_tablespace', changes[0].tablespace
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'angry', changes[0]. remarks

    def columns = changes[0].columns
    assertNotNull columns
    assertEquals 2, columns.size()
    assertTrue columns[0] instanceof ColumnConfig
    assertTrue columns[1] instanceof ColumnConfig
    assertEquals 'status', columns[0].name
    assertEquals 'varchar(100)', columns[0].type
    assertEquals 'id', columns[1].name
    assertEquals 'int', columns[1].type
  }


  @Test
  void renameTable() {
    buildChangeSet {
      renameTable(schemaName: 'schema', oldTableName: 'fail_table', newTableName: 'win_table')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof RenameTableChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'fail_table', changes[0].oldTableName
    assertEquals 'win_table', changes[0].newTableName
  }


  @Test
  void dropTable() {
    buildChangeSet {
      dropTable(schemaName: 'schema', tableName: 'fail_table')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropTableChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'fail_table', changes[0].tableName
  }


  @Test
  void createView() {
    buildChangeSet {
      createView(schemaName: 'schema', viewName: 'monkey_view', replaceIfExists: true) {
        "SELECT * FROM monkey WHERE state='angry'"
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof CreateViewChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'monkey_view', changes[0].viewName
    assertTrue changes[0].replaceIfExists
    assertEquals "SELECT * FROM monkey WHERE state='angry'", changes[0].selectQuery
  }


  @Test
  void renameView() {
    buildChangeSet {
      renameView(schemaName: 'schema', oldViewName: 'fail_view', newViewName: 'win_view')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof RenameViewChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'fail_view', changes[0].oldViewName
    assertEquals 'win_view', changes[0].newViewName
  }


  @Test
  void dropView() {
    buildChangeSet {
      dropView(schemaName: 'schema', viewName: 'fail_view')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropViewChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'fail_view', changes[0].viewName
  }


  @Test
  void mergeColumns() {
   // mergeColumns(schemaName: '', tableName: '', column1Name: '', column2Name: '', finalColumnName: '', finalColumnType: '', joinString: ' ')
    buildChangeSet {
      mergeColumns(schemaName: 'schema', tableName: 'table', column1Name: 'first_name', column2Name: 'last_name', finalColumnName: 'full_name', finalColumnType: 'varchar(99)', joinString: ' ')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof MergeColumnChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'table', changes[0].tableName
    assertEquals 'first_name', changes[0].column1Name
    assertEquals 'last_name', changes[0].column2Name
    assertEquals 'full_name', changes[0].finalColumnName
    assertEquals 'varchar(99)', changes[0].finalColumnType
    assertEquals ' ', changes[0].joinString
  }


  @Test
  void createStoredProcedure() {
    def sql = """\
CREATE OR REPLACE PROCEDURE testMonkey
IS
BEGIN
 -- do something with the monkey
END;"""
    buildChangeSet {
      createStoredProcedure sql
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof CreateProcedureChange
    assertEquals sql, changes[0].procedureBody
  }

}
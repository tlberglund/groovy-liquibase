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

import com.augusttechgroup.liquibase.GroovyLiquibaseChangeLogParser

import liquibase.parser.ChangeLogParserFactory
import liquibase.resource.FileSystemResourceAccessor
import liquibase.changelog.ChangeSet
import liquibase.change.core.AddColumnChange

import org.junit.Test
import org.junit.Before
import org.junit.Ignore
import static org.junit.Assert.*
import liquibase.change.core.RenameColumnChange
import liquibase.change.core.DropColumnChange
import liquibase.change.core.AlterSequenceChange
import liquibase.change.core.CreateTableChange
import liquibase.change.ColumnConfig


class StructuralRefactoringTests {

  def resourceAccessor
  def parserFactory
  def changeSet
    

  @Before
  void registerParser() {
    resourceAccessor = new FileSystemResourceAccessor(baseDirectory: '.')
    parserFactory = ChangeLogParserFactory.instance
    ChangeLogParserFactory.getInstance().register(new GroovyLiquibaseChangeLogParser())

		changeSet = new ChangeSet(
		  'generic-changeset-id',
		  'tlberglund',
		  false,
		  false,
		  '/filePath',
		  '/physicalFilePath',
		  'context',
		  'mysql',
		  true)
  }

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

  private def buildChangeSet(Closure closure) {
    closure.delegate = new ChangeSetDelegate(changeSet: changeSet)
    closure.call()
    changeSet
  }
  
}
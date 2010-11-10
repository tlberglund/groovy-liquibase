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

import org.junit.Test
import org.junit.Before
import static org.junit.Assert.*

import liquibase.changelog.ChangeSet
import liquibase.change.core.InsertDataChange
import liquibase.change.ColumnConfig
import java.sql.Timestamp
import liquibase.change.core.LoadDataChange
import liquibase.change.core.LoadDataColumnConfig
import liquibase.change.core.LoadUpdateDataChange
import liquibase.change.core.UpdateDataChange


class NonRefactoringTransformationTests
{
  def changeSet


  @Before
  void registerParser() {
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


  @Test
  void insertData() {
    //TODO make these tests timezone-insensitive
    def now = '2010-11-02 07:52:04'
    def sqlNow = new Timestamp(1288702324000)
    buildChangeSet {
      insert(schemaName: 'schema', tableName: 'monkey') {
        column(name: 'id', valueNumeric: 502)
        column(name: 'emotion', value: 'angry')
        column(name: 'last_updated', valueDate: now)
        column(name: 'active', valueBoolean: true)
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof InsertDataChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    def columns = changes[0].columns
    assertNotNull columns
    assertTrue columns.every { column -> column instanceof ColumnConfig}
    assertEquals 4, columns.size()
    assertEquals 'id', columns[0].name
    assertEquals 502, columns[0].valueNumeric
    assertEquals 'emotion', columns[1].name
    assertEquals 'angry', columns[1].value
    assertEquals 'last_updated', columns[2].name
    assertEquals sqlNow, columns[2].valueDate
    assertEquals 'active', columns[3].name
    assertTrue columns[3].valueBoolean
  }


  @Test
  void loadDataFromFilenameUsingColumnNames() {
    buildChangeSet {
      loadData(schemaName: 'schema', tableName: 'monkey', file: 'data.csv', encoding: 'UTF-8') {
        column(header: 'header_id', name: 'id', type: 'NUMERIC')
        column(header: 'header_emotion', name: 'emotion', type: 'STRING')
        column(header: 'header_last_updated', name: 'last_updated', type: 'DATETIME')
        column(header: 'header_active', name: 'active', type: 'BOOLEAN')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof LoadDataChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'data.csv', changes[0].file
    assertEquals 'UTF-8', changes[0].encoding
    def columns = changes[0].columns
    assertNotNull columns
    assertTrue columns.every { column -> column instanceof LoadDataColumnConfig}
    assertEquals 4, columns.size()
    assertEquals 'id', columns[0].name
    assertEquals 'header_id', columns[0].header
    assertEquals 'NUMERIC', columns[0].type
    assertEquals 'emotion', columns[1].name
    assertEquals 'header_emotion', columns[1].header
    assertEquals 'STRING', columns[1].type
    assertEquals 'last_updated', columns[2].name
    assertEquals 'header_last_updated', columns[2].header
    assertEquals 'DATETIME', columns[2].type
    assertEquals 'active', columns[3].name
    assertEquals 'header_active', columns[3].header
    assertEquals 'BOOLEAN', columns[3].type
  }


  @Test
  void loadDataFromFilenameUsingIndexes() {
    buildChangeSet {
      loadData(schemaName: 'schema', tableName: 'monkey', file: 'data.csv', encoding: 'UTF-8') {
        column(index: 0, name: 'id', type: 'NUMERIC')
        column(index: 1, name: 'emotion', type: 'STRING')
        column(index: 2, name: 'last_updated', type: 'DATETIME')
        column(index: 3, name: 'active', type: 'BOOLEAN')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof LoadDataChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'data.csv', changes[0].file
    assertEquals 'UTF-8', changes[0].encoding
    def columns = changes[0].columns
    assertNotNull columns
    assertTrue columns.every { column -> column instanceof LoadDataColumnConfig}
    assertEquals 4, columns.size()
    assertEquals 'id', columns[0].name
    assertEquals 0, columns[0].index
    assertEquals 'NUMERIC', columns[0].type
    assertEquals 'emotion', columns[1].name
    assertEquals 1, columns[1].index
    assertEquals 'STRING', columns[1].type
    assertEquals 'last_updated', columns[2].name
    assertEquals 2, columns[2].index
    assertEquals 'DATETIME', columns[2].type
    assertEquals 'active', columns[3].name
    assertEquals 3, columns[3].index
    assertEquals 'BOOLEAN', columns[3].type
  }


  @Test
  void loadDataFromFileUsingColumnNames() {
    buildChangeSet {
      loadData(schemaName: 'schema', tableName: 'monkey', file: new File('data.csv'), encoding: 'UTF-8') {
        column(header: 'header_emotion', name: 'emotion', type: 'STRING')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof LoadDataChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals new File('data.csv').canonicalPath, changes[0].file
    assertEquals 'UTF-8', changes[0].encoding
    def columns = changes[0].columns
    assertNotNull columns
    assertTrue columns.every { column -> column instanceof LoadDataColumnConfig}
    assertEquals 1, columns.size()
    assertEquals 'emotion', columns[0].name
    assertEquals 'header_emotion', columns[0].header
    assertEquals 'STRING', columns[0].type
  }



  @Test
  void loadUpdateDataFromFilenameUsingColumnNames() {
    buildChangeSet {
      loadUpdateData(schemaName: 'schema', tableName: 'monkey', file: 'data.csv', encoding: 'UTF-8', primaryKey: 'id') {
        column(header: 'header_id', name: 'id', type: 'NUMERIC')
        column(header: 'header_emotion', name: 'emotion', type: 'STRING')
        column(header: 'header_last_updated', name: 'last_updated', type: 'DATETIME')
        column(header: 'header_active', name: 'active', type: 'BOOLEAN')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof LoadUpdateDataChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'data.csv', changes[0].file
    assertEquals 'UTF-8', changes[0].encoding
    assertEquals 'id', changes[0].primaryKey
    def columns = changes[0].columns
    assertNotNull columns
    assertTrue columns.every { column -> column instanceof LoadDataColumnConfig}
    assertEquals 4, columns.size()
    assertEquals 'id', columns[0].name
    assertEquals 'header_id', columns[0].header
    assertEquals 'NUMERIC', columns[0].type
    assertEquals 'emotion', columns[1].name
    assertEquals 'header_emotion', columns[1].header
    assertEquals 'STRING', columns[1].type
    assertEquals 'last_updated', columns[2].name
    assertEquals 'header_last_updated', columns[2].header
    assertEquals 'DATETIME', columns[2].type
    assertEquals 'active', columns[3].name
    assertEquals 'header_active', columns[3].header
    assertEquals 'BOOLEAN', columns[3].type
  }


  @Test
  void loadUpdateDataFromFilenameUsingIndexes() {
    buildChangeSet {
      loadUpdateData(schemaName: 'schema', tableName: 'monkey', file: 'data.csv', encoding: 'UTF-8', primaryKey: 'id') {
        column(index: 0, name: 'id', type: 'NUMERIC')
        column(index: 1, name: 'emotion', type: 'STRING')
        column(index: 2, name: 'last_updated', type: 'DATETIME')
        column(index: 3, name: 'active', type: 'BOOLEAN')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof LoadUpdateDataChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'data.csv', changes[0].file
    assertEquals 'UTF-8', changes[0].encoding
    assertEquals 'id', changes[0].primaryKey
    def columns = changes[0].columns
    assertNotNull columns
    assertTrue columns.every { column -> column instanceof LoadDataColumnConfig}
    assertEquals 4, columns.size()
    assertEquals 'id', columns[0].name
    assertEquals 0, columns[0].index
    assertEquals 'NUMERIC', columns[0].type
    assertEquals 'emotion', columns[1].name
    assertEquals 1, columns[1].index
    assertEquals 'STRING', columns[1].type
    assertEquals 'last_updated', columns[2].name
    assertEquals 2, columns[2].index
    assertEquals 'DATETIME', columns[2].type
    assertEquals 'active', columns[3].name
    assertEquals 3, columns[3].index
    assertEquals 'BOOLEAN', columns[3].type
  }


  @Test
  void loadUpdateDataFromFileUsingColumnNames() {
    buildChangeSet {
      loadUpdateData(schemaName: 'schema', tableName: 'monkey', file: new File('data.csv'), encoding: 'UTF-8', primaryKey: 'id') {
        column(header: 'header_emotion', name: 'emotion', type: 'STRING')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof LoadUpdateDataChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals new File('data.csv').canonicalPath, changes[0].file
    assertEquals 'UTF-8', changes[0].encoding
    assertEquals 'id', changes[0].primaryKey
    def columns = changes[0].columns
    assertNotNull columns
    assertTrue columns.every { column -> column instanceof LoadDataColumnConfig}
    assertEquals 1, columns.size()
    assertEquals 'emotion', columns[0].name
    assertEquals 'header_emotion', columns[0].header
    assertEquals 'STRING', columns[0].type
  }


  @Test
  void updateData() {
    //TODO make these tests timezone-insensitive
    def now = '2010-11-02 07:52:04'
    def sqlNow = new Timestamp(1288702324000)
    buildChangeSet {
      update(schemaName: 'schema', tableName: 'monkey') {
        column(name: 'rfid_tag', valueNumeric: 5023442)
        column(name: 'emotion', value: 'angry')
        column(name: 'last_updated', valueDate: now)
        column(name: 'active', valueBoolean: true)
        where "id=882"
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof UpdateDataChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'id=882', changes[0].whereClause
    def columns = changes[0].columns
    assertNotNull columns
    assertTrue columns.every { column -> column instanceof ColumnConfig}
    assertEquals 4, columns.size()
    assertEquals 'rfid_tag', columns[0].name
    assertEquals 5023442, columns[0].valueNumeric
    assertEquals 'emotion', columns[1].name
    assertEquals 'angry', columns[1].value
    assertEquals 'last_updated', columns[2].name
    assertEquals sqlNow, columns[2].valueDate
    assertEquals 'active', columns[3].name
    assertTrue columns[3].valueBoolean
  }


  @Test
  void deleteData() {
    buildChangeSet {
      delete(schemaName: 'schema', tableName: 'monkey') {
        where "emotion='angry' AND active=true"
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof UpdateDataChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals "emotion='angry' AND active=true", changes[0].whereClause
  }


  private def buildChangeSet(Closure closure) {
    closure.delegate = new ChangeSetDelegate(changeSet: changeSet)
    closure.call()
    changeSet
  }

}

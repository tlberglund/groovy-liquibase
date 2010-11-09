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
import liquibase.change.core.AddForeignKeyConstraintChange
import liquibase.change.core.DropForeignKeyConstraintChange
import liquibase.change.core.AddPrimaryKeyChange
import liquibase.change.core.DropPrimaryKeyChange
import liquibase.change.core.InsertDataChange
import liquibase.change.ColumnConfig
import java.sql.Timestamp


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
    def now = '2010-11-02 06:52:04'
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
    assertEquals 'id', columns[0].name
    assertEquals 502, columns[0].valueNumeric
    assertEquals 'emotion', columns[1].name
    assertEquals 'angry', columns[1].value
    assertEquals 'last_updated', columns[2].name
    assertEquals sqlNow, columns[2].valueDate
    assertEquals 'active', columns[3].name
    assertTrue columns[3].valueBoolean
  }




  private def buildChangeSet(Closure closure) {
    closure.delegate = new ChangeSetDelegate(changeSet: changeSet)
    closure.call()
    changeSet
  }

}

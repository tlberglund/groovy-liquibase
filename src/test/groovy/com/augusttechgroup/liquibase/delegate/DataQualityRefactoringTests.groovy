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
import liquibase.change.core.AddLookupTableChange
import liquibase.change.core.AddNotNullConstraintChange


class DataQualityRefactoringTests
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
  void addLookupTable() {
    buildChangeSet {
      addLookupTable(
        existingTableName: 'monkey',
        existingTableSchemaName: 'old_schema',
        existingColumnName: 'emotion',
        newTableName: 'monkey_emotion',
        newTableSchemaName: 'new_schema',
        newColumnName: 'emotion_display',
        newColumnDataType: 'varchar(50)',
        constraintName: 'fk_monkey_emotion'
      )
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddLookupTableChange
    assertEquals 'monkey', changes[0].existingTableName
    assertEquals 'old_schema', changes[0].existingTableSchemaName
    assertEquals 'emotion', changes[0].existingColumnName
    assertEquals 'monkey_emotion', changes[0].newTableName
    assertEquals 'new_schema', changes[0].newTableSchemaName
    assertEquals 'emotion_display', changes[0].newColumnName
    assertEquals 'varchar(50)', changes[0].newColumnDataType
    assertEquals 'fk_monkey_emotion', changes[0].constraintName
  }


  @Test
  void notNullConstraint() {
    buildChangeSet {
      addNotNullConstraint(schemaName: 'schema', tableName: 'monkey', columnName: 'emotion', defaultNullValue: 'angry', columnDataType: 'varchar(75)')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddNotNullConstraintChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'emotion', changes[0].columnName
    assertEquals 'angry', changes[0].defaultNullValue
    assertEquals 'varchar(75)', changes[0].columnDataType

  }

  private def buildChangeSet(Closure closure) {
    closure.delegate = new ChangeSetDelegate(changeSet: changeSet)
    closure.call()
    changeSet
  }
  
}
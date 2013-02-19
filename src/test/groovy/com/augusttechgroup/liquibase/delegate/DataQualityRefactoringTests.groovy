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

import org.junit.Test
import static org.junit.Assert.*

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


class DataQualityRefactoringTests
  extends ChangeSetTests
{

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


  @Test
  void dropNotNullConstraint() {
    buildChangeSet {
      dropNotNullConstraint(schemaName: 'schema', tableName: 'monkey', columnName: 'emotion', columnDataType: 'varchar(75)')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropNotNullConstraintChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'emotion', changes[0].columnName
    assertEquals 'varchar(75)', changes[0].columnDataType
  }


  @Test
  void addUniqueConstraint() {
    buildChangeSet {
      addUniqueConstraint(tablespace: 'tablespace', schemaName: 'schema', tableName: 'monkey', columnNames: 'species, emotion', constraintName: 'unique_constraint')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddUniqueConstraintChange
    assertEquals 'tablespace', changes[0].tablespace
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'species, emotion', changes[0].columnNames
    assertEquals 'unique_constraint', changes[0].constraintName
  }


  @Test
  void dropUniqueConstraint() {
    buildChangeSet {
      dropUniqueConstraint(tableName: 'table', schemaName: 'schema', constraintName: 'unique_constraint')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropUniqueConstraintChange
    assertEquals 'table', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'unique_constraint', changes[0].constraintName
  }


  @Test
  void createSequence() {
    buildChangeSet {
      createSequence(sequenceName: 'sequence', schemaName: 'schema', incrementBy: 42, minValue: 7, maxValue: 6.023E24, ordered: true, startValue: 8)
    }

    def changes = changeSet.changes 
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof CreateSequenceChange
    assertEquals 'sequence', changes[0].sequenceName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 42G, changes[0].incrementBy
    assertEquals 7G, changes[0].minValue
    assertEquals 6023000000000000000000000, changes[0].maxValue
    assertEquals 8G, changes[0].startValue
    assertTrue changes[0].ordered
  }


  @Test
  void dropSequence() {
    buildChangeSet {
      dropSequence(sequenceName: 'sequence')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropSequenceChange
    assertEquals 'sequence', changes[0].sequenceName
  }

  @Test
  void createTypeSafeSequence() {
    buildChangeSet {
      createSequence(sequenceName: 'typeSafeSequence', schemaName: 'schema', incrementBy: 42, minValue: 7, maxValue: "999999999999999999999999999", ordered: true, startValue: "1000000")
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof CreateSequenceChange
    assertEquals 'typeSafeSequence', changes[0].sequenceName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 42G, changes[0].incrementBy
    assertEquals 7G, changes[0].minValue
    assertEquals 999999999999999999999999999 as BigInteger, changes[0].maxValue
    assertEquals 1000000 as BigInteger, changes[0].startValue
    assertTrue changes[0].ordered
  }

  @Test
  void dropTypeSafeSequence() {
    buildChangeSet {
      dropSequence(sequenceName: 'typeSafeSequence')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropSequenceChange
    assertEquals 'typeSafeSequence', changes[0].sequenceName
  }

  @Test
  void addAutoIncrement() {
    buildChangeSet {
      addAutoIncrement(tableName: 'monkey', columnName: 'angry', columnDataType: 'boolean')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddAutoIncrementChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'angry', changes[0].columnName
    assertEquals 'boolean', changes[0].columnDataType
  }


  @Test
  void addDefaultValueString() {
    buildChangeSet {
      addDefaultValue(tableName: 'monkey', schemaName: 'schema', columnName: 'emotion', defaultValue: 'angry')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddDefaultValueChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'emotion', changes[0].columnName
    assertEquals 'angry', changes[0].defaultValue
  }


  @Test
  void addDefaultValueNumeric() {
    buildChangeSet {
      addDefaultValue(tableName: 'monkey', schemaName: 'schema', columnName: 'strength', defaultValueNumeric: '2.718281828459045')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddDefaultValueChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'strength', changes[0].columnName
    assertEquals '2.718281828459045', changes[0].defaultValueNumeric
  }


  @Test
  void addDefaultValueBoolean() {
    buildChangeSet {
      addDefaultValue(tableName: 'monkey', schemaName: 'schema', columnName: 'strength', defaultValueBoolean: true)
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddDefaultValueChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'strength', changes[0].columnName
    assertTrue changes[0].defaultValueBoolean
  }


  @Test
  void addDefaultValueDate() {
    buildChangeSet {
      addDefaultValue(tableName: 'monkey', schemaName: 'schema', columnName: 'birthdate', defaultValueDate: '20101109T130400Z')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddDefaultValueChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'birthdate', changes[0].columnName
    assertEquals '20101109T130400Z', changes[0].defaultValueDate
  }


  @Test
  void dropDefaultValueDate() {
    buildChangeSet {
      dropDefaultValue(tableName: 'monkey', schemaName: 'schema', columnName: 'emotion')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropDefaultValueChange
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'emotion', changes[0].columnName
  }

}

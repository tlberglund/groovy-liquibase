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

import liquibase.change.core.AddForeignKeyConstraintChange
import liquibase.change.core.DropForeignKeyConstraintChange
import liquibase.change.core.AddPrimaryKeyChange
import liquibase.change.core.DropPrimaryKeyChange


class ReferentialIntegrityRefactoringTests
  extends ChangeSetTests
{

  @Test
  void addForeignKeyConstraint() {
    buildChangeSet {
      addForeignKeyConstraint(constraintName: 'fk_monkey_emotion', baseTableName: 'monkey', baseTableSchemaName: 'base_schema', baseColumnNames: 'emotion_id', referencedTableName: 'emotions', referencedTableSchemaName: 'referenced_schema', referencedColumnNames: 'id', deferrable: true, initiallyDeferred: true, onDelete: 'CASCADE', onUpdate: 'CASCADE', referencesUniqueColumn: false)
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddForeignKeyConstraintChange
    assertEquals 'fk_monkey_emotion', changes[0].constraintName
    assertEquals 'monkey', changes[0].baseTableName
    assertEquals 'base_schema', changes[0].baseTableSchemaName
    assertEquals 'emotion_id', changes[0].baseColumnNames
    assertEquals 'emotions', changes[0].referencedTableName
    assertEquals 'referenced_schema', changes[0].referencedTableSchemaName
    assertEquals 'id', changes[0].referencedColumnNames
    assertTrue changes[0].deferrable
    assertTrue changes[0].initiallyDeferred
    assertEquals 'CASCADE', changes[0].onDelete
    assertEquals 'CASCADE', changes[0].onUpdate
    assertFalse changes[0].referencesUniqueColumn
  }



  @Test
  void addForeignKeyConstraintWithDeleteCascadeProperty() {
    buildChangeSet {
      addForeignKeyConstraint(constraintName: 'fk_monkey_emotion', baseTableName: 'monkey', baseTableSchemaName: 'base_schema', baseColumnNames: 'emotion_id', referencedTableName: 'emotions', referencedTableSchemaName: 'referenced_schema', referencedColumnNames: 'id', deferrable: true, initiallyDeferred: true, deleteCascade: true, onUpdate: 'CASCADE', referencesUniqueColumn: true)
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddForeignKeyConstraintChange
    assertEquals 'fk_monkey_emotion', changes[0].constraintName
    assertEquals 'monkey', changes[0].baseTableName
    assertEquals 'base_schema', changes[0].baseTableSchemaName
    assertEquals 'emotion_id', changes[0].baseColumnNames
    assertEquals 'emotions', changes[0].referencedTableName
    assertEquals 'referenced_schema', changes[0].referencedTableSchemaName
    assertEquals 'id', changes[0].referencedColumnNames
    assertTrue changes[0].deferrable
    assertTrue changes[0].initiallyDeferred
    assertEquals 'CASCADE', changes[0].onDelete
    assertEquals 'CASCADE', changes[0].onUpdate
    assertTrue changes[0].referencesUniqueColumn
  }


  @Test
  void dropForeignKeyConstraint() {
    buildChangeSet {
      dropForeignKeyConstraint(constraintName: 'fk_monkey__emotion', baseTableName: 'monkey', baseTableSchemaName: 'schema')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropForeignKeyConstraintChange
    assertEquals 'fk_monkey__emotion', changes[0].constraintName
    assertEquals 'monkey', changes[0].baseTableName
    assertEquals 'schema', changes[0].baseTableSchemaName
  }


  @Test
  void addPrimaryKey() {
    buildChangeSet {
      addPrimaryKey(tableName: 'monkey', schemaName: 'schema', columnNames: 'id', constraintName: 'pk_monkey', tablespace: 'tablespace')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddPrimaryKeyChange
    assertEquals 'pk_monkey', changes[0].constraintName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'tablespace', changes[0].tablespace
    assertEquals 'id', changes[0].columnNames
  }


  @Test
  void dropPrimaryKey() {
    buildChangeSet {
      dropPrimaryKey(tableName: 'monkey', schemaName: 'schema', constraintName: 'pk_monkey')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropPrimaryKeyChange
    assertEquals 'pk_monkey', changes[0].constraintName
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
  }

}

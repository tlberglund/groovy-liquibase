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
import liquibase.change.ColumnConfig
import liquibase.change.core.CreateIndexChange
import liquibase.change.core.DropIndexChange

class ArchitecturalRefactoringTests
  extends ChangeSetTests
{

  @Test
  void createIndexWithMultipleColumns() {
    buildChangeSet {
      createIndex(schemaName: 'schema', tableName: 'monkey', tablespace: 'tablespace', indexName: 'ndx_monkeys', unique: true) {
        column(name: 'species')
        column(name: 'name')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof CreateIndexChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'tablespace', changes[0].tablespace
    assertEquals 'ndx_monkeys', changes[0].indexName
    assertTrue changes[0].unique
    def columns = changes[0].columns
    assertNotNull columns
    assertTrue columns.every { column -> column instanceof ColumnConfig}
    assertEquals 2, columns.size()
    assertEquals 'species', columns[0].name
    assertEquals 'name', columns[1].name
  }


  @Test
  void createIndexWithOneColumn() {
    buildChangeSet {
      createIndex(schemaName: 'schema', tableName: 'monkey', tablespace: 'tablespace', indexName: 'ndx_monkeys', unique: true) {
        column(name: 'name')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof CreateIndexChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'tablespace', changes[0].tablespace
    assertEquals 'ndx_monkeys', changes[0].indexName
    assertTrue changes[0].unique
    def columns = changes[0].columns
    assertNotNull columns
    assertTrue columns.every { column -> column instanceof ColumnConfig}
    assertEquals 1, columns.size()
    assertEquals 'name', columns[0].name
  }


  @Test
  void dropIndex() {
    buildChangeSet {
      dropIndex(schemaName: 'schema', tableName: 'monkey', indexName: 'ndx_monkeys')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropIndexChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'schema', changes[0].schemaName
    assertEquals 'ndx_monkeys', changes[0].indexName
  }

}


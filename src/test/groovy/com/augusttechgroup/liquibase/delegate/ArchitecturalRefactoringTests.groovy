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
import static org.junit.Assert.*
import liquibase.change.ColumnConfig
import liquibase.change.core.CreateIndexChange
import liquibase.change.core.DropIndexChange

class ArchitecturalRefactoringTests
  extends ChangeSetTests
{

  @Test
  void createIndexWithOneColumn() {
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
  void createIndexWithMultipleColumns() {
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
      dropIndex(tableName: 'monkey', indexName: 'ndx_monkeys')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof DropIndexChange
    assertEquals 'monkey', changes[0].tableName
    assertEquals 'ndx_monkeys', changes[0].indexName
  }

}


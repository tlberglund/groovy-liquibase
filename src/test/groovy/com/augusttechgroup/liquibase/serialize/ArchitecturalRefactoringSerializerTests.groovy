//
// com.augusttechgroup.liquibase.serialize
// Copyright (C) 2010 
// ALL RIGHTS RESERVED
//

package com.augusttechgroup.liquibase.serialize

import org.junit.Test
import static org.junit.Assert.*
import liquibase.change.ColumnConfig
import liquibase.change.core.CreateIndexChange
import liquibase.change.core.DropIndexChange
import org.junit.Ignore


class ArchitecturalRefactoringSerializerTests
  extends SerializerTests
{

  @Test
  void createIndexWithMultipleColumns() {
    def change = [
      tableName: 'monkey',
      schemaName: 'schema',
      tablespace: 'tablespace',
      indexName: 'ndx_monkeys',
      unique: true,
      columns: [
        [ name: 'species' ] as ColumnConfig,
        [ name: 'name' ] as ColumnConfig
      ]
    ] as CreateIndexChange

    def serializedText = serializer.serialize(change)
    def expectedText = """\
createIndex(indexName: 'ndx_monkeys', schemaName: 'schema', tableName: 'monkey', tablespace: 'tablespace', unique: true) {
  column(name: 'species')
  column(name: 'name')
}"""
    assertEquals expectedText, serializedText
  }


  @Ignore
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


  @Ignore
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


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


  @Test
  void createIndexWithOneColumn() {
    def change = [
      tableName: 'monkey',
      schemaName: 'schema',
      tablespace: 'tablespace',
      indexName: 'ndx_monkeys',
      unique: true,
      columns: [ [ name: 'name' ] as ColumnConfig ]
    ] as CreateIndexChange

    def serializedText = serializer.serialize(change)
    def expectedText = """\
createIndex(indexName: 'ndx_monkeys', schemaName: 'schema', tableName: 'monkey', tablespace: 'tablespace', unique: true) {
  column(name: 'name')
}"""
    assertEquals expectedText, serializedText
  }


  @Test
  void dropIndex() {
    def change = [
      tableName: 'monkey',
      indexName: 'ndx_monkeys'
    ] as DropIndexChange

    def serializedText = serializer.serialize(change)
    def expectedText = "dropIndex(indexName: 'ndx_monkeys', tableName: 'monkey')"
    assertEquals expectedText, serializedText
  }

}


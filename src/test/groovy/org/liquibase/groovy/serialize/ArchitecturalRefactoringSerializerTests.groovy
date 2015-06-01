/*
 * Copyright 2011-2015 Tim Berglund and Steven C. Saliman
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

package org.liquibase.groovy.serialize

import org.junit.Test
import static org.junit.Assert.*
import liquibase.change.ColumnConfig
import liquibase.change.core.CreateIndexChange
import liquibase.change.core.DropIndexChange


class ArchitecturalRefactoringSerializerTests extends SerializerTests {

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

    def serializedText = serializer.serialize(change, true)
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

    def serializedText = serializer.serialize(change, true)
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

    def serializedText = serializer.serialize(change, true)
    def expectedText = "dropIndex(indexName: 'ndx_monkeys', tableName: 'monkey')"
    assertEquals expectedText, serializedText
  }

}


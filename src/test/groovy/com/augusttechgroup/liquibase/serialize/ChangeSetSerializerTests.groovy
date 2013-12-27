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

package com.augusttechgroup.liquibase.serialize

import liquibase.changelog.DatabaseChangeLog
import org.junit.Test
import static org.junit.Assert.*
import liquibase.changelog.ChangeSet
import liquibase.change.core.DropTableChange
import liquibase.change.core.AddForeignKeyConstraintChange



class ChangeSetSerializerTests
  extends SerializerTests
{

  @Test
  void serializeSimpleChangeSet() {
    def changeSet = new ChangeSet(
      'drop-table',
      'tlberglund',
      false,
      false,
      '.',
      null,
      null,
      true,
      new DatabaseChangeLog())
    changeSet.addChange([ schemaName: 'schema', tableName: 'monkey' ] as DropTableChange)

    def serializedText = serializer.serialize(changeSet, true)
    def expectedText = """\
changeSet(id: 'drop-table', author: 'tlberglund') {
  dropTable(schemaName: 'schema', tableName: 'monkey')
}"""
    assertEquals expectedText, serializedText
  }

  @Test
  void serializeCompleteChangeSet() {
    def comment = "This is a Liquibase comment by Tim \\\"Tim\\\" Berglund"
    def changeSet = new ChangeSet(
      'drop-table',
      'tlberglund',
      true,
      true,
      '.',
      'dev, staging',
      'mysql, oracle',
      true,
      new DatabaseChangeLog())
    changeSet.addChange([ schemaName: 'schema', tableName: 'monkey' ] as DropTableChange)
    changeSet.addChange([ constraintName: 'fk_monkey_emotion', baseTableName: 'monkey', baseTableSchemaName: 'base_schema', baseColumnNames: 'emotion_id', referencedTableName: 'emotions', referencedTableSchemaName: 'referenced_schema', referencedColumnNames: 'id', deferrable: true, initiallyDeferred: true, onDelete: 'CASCADE', onUpdate: 'CASCADE' ] as AddForeignKeyConstraintChange)
    changeSet.comments = comment

    def serializedText = serializer.serialize(changeSet, true)
    def expectedText = """\
changeSet(id: 'drop-table', author: 'tlberglund', runAlways: true, runOnChange: true, context: 'staging,dev', dbms: 'oracle,mysql') {
  comment "${comment}"
  dropTable(schemaName: 'schema', tableName: 'monkey')
  addForeignKeyConstraint(baseColumnNames: 'emotion_id', baseTableName: 'monkey', baseTableSchemaName: 'base_schema', constraintName: 'fk_monkey_emotion', deferrable: true, initiallyDeferred: true, onDelete: 'CASCADE', onUpdate: 'CASCADE', referencedColumnNames: 'id', referencedTableName: 'emotions', referencedTableSchemaName: 'referenced_schema')
}"""
    assertEquals expectedText.toString(), serializedText
  }


}

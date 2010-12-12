//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

package com.augusttechgroup.liquibase.serialize

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
      '.',
      null,
      null,
      true)
    changeSet.addChange([ schemaName: 'schema', tableName: 'monkey' ] as DropTableChange)

    def serializedText = serializer.serialize(changeSet)
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
      '.',
      'dev, staging',
      'mysql, oracle',
      true)
    changeSet.addChange([ schemaName: 'schema', tableName: 'monkey' ] as DropTableChange)
    changeSet.addChange([ constraintName: 'fk_monkey_emotion', baseTableName: 'monkey', baseTableSchemaName: 'base_schema', baseColumnNames: 'emotion_id', referencedTableName: 'emotions', referencedTableSchemaName: 'referenced_schema', referencedColumnNames: 'id', deferrable: true, initiallyDeferred: true, onDelete: 'CASCADE', onUpdate: 'CASCADE' ] as AddForeignKeyConstraintChange)
    changeSet.comments = comment

    def serializedText = serializer.serialize(changeSet)
    def expectedText = """\
changeSet(id: 'drop-table', author: 'tlberglund', runAlways: true, runOnChange: true, context: 'staging,dev', dbms: 'oracle,mysql') {
  comment "${comment}"
  dropTable(schemaName: 'schema', tableName: 'monkey')
  addForeignKeyConstraint(baseColumnNames: 'emotion_id', baseTableName: 'monkey', baseTableSchemaName: 'base_schema', constraintName: 'fk_monkey_emotion', deferrable: true, initiallyDeferred: true, onDelete: 'CASCADE', onUpdate: 'CASCADE', referencedColumnNames: 'id', referencedTableName: 'emotions', referencedTableSchemaName: 'referenced_schema')
}"""
    assertEquals expectedText.toString(), serializedText
  }


}

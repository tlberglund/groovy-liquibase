//
// com.augusttechgroup.liquibase.serialize
// Copyright (C) 2010 
// ALL RIGHTS RESERVED
//

package com.augusttechgroup.liquibase.serialize

import org.junit.Test
import liquibase.change.ColumnConfig
import static org.junit.Assert.*
import liquibase.change.ConstraintsConfig


/**
 * <p></p>
 * 
 * @author Tim Berglund
 */
class ColumnSerializerTests
  extends SerializerTests
{

  @Test
  void buildSimpleStringColumn() {
    def columnConfig = new ColumnConfig()
    columnConfig.name = 'column-name'
    columnConfig.type = 'varchar'
    columnConfig.value = 'value'
    columnConfig.defaultValue = 'default-string-value'
    columnConfig.remarks = 'No comment'

    def serializedText = serializer.serialize(columnConfig)
    def expectedText = "column(name: 'column-name', type: 'varchar', value: 'value', defaultValue: 'default-string-value', remarks: 'No comment')"
    assertEquals expectedText, serializedText
  }


  @Test
  void buildSimpleNumericColumn() {
    def columnConfig = new ColumnConfig()
    columnConfig.name = 'column-name'
    columnConfig.type = 'int'
    columnConfig.valueNumeric = 3
    columnConfig.defaultValueNumeric = 42
    columnConfig.autoIncrement = false

    def serializedText = serializer.serialize(columnConfig)
    def expectedText = "column(name: 'column-name', type: 'int', valueNumeric: 3, defaultValueNumeric: 42, autoIncrement: false)"
    assertEquals expectedText, serializedText
  }


  @Test
  void buildSimpleDateColumn() {
    def now = "2010-11-02T07:52:04.0"
    def then = "2010-11-28T22:09:43.0"
    def sqlNow = parseSqlTimestamp(now)
    def sqlThen = parseSqlTimestamp(then)

    def columnConfig = new ColumnConfig()
    columnConfig.name = 'column-name'
    columnConfig.type = 'datetime'
    columnConfig.valueDate = now
    columnConfig.defaultValueDate = then
    columnConfig.remarks = 'No date comment'

    def serializedText = serializer.serialize(columnConfig)
    def expectedText = "column(name: 'column-name', type: 'datetime', valueDate: '${now}', defaultValueDate: '${then}', remarks: 'No date comment')"
    assertEquals expectedText as String, serializedText
  }


  @Test
  void buildSimpleBooleanColumn() {
    def columnConfig = new ColumnConfig()
    columnConfig.name = 'column-name'
    columnConfig.type = 'bit'
    columnConfig.valueBoolean = true
    columnConfig.defaultValueBoolean = false
    columnConfig.remarks = 'No boolean comment'

    def serializedText = serializer.serialize(columnConfig)
    def expectedText = "column(name: 'column-name', type: 'bit', valueBoolean: true, defaultValueBoolean: false, remarks: 'No boolean comment')"
  }


  @Test
  void buildStringColumnWithConstraintsInMap() {
    // This is an unprofessional comment, but: Groovy is *awesome*
    def columnConfig = [
      name: 'column-name',
      type: 'varchar',
      value: 'value',
      defaultValue: 'default-string-value',
      remarks: 'No comment',
      constraints: [
        nullable: false,
        unique: true,
        uniqueConstraintName: 'unique_monkey_constraint'
      ] as ConstraintsConfig
    ] as ColumnConfig
    
    def serializedText = serializer.serialize(columnConfig)
    def expectedText = """\
column(name: 'column-name', type: 'varchar', value: 'value', defaultValue: 'default-string-value', remarks: 'No comment') {
  constraints(nullable: false, unique: true, uniqueConstraintName: 'unique_monkey_constraint')
}"""
    assertEquals expectedText, serializedText
  }

}

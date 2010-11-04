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

import liquibase.change.ColumnConfig

import org.junit.Test
import static org.junit.Assert.*
import java.sql.Timestamp
import liquibase.change.ConstraintsConfig


class ColumnDelegateTests
{

  @Test
  void buildSimpleStringColumn() {
    def closure = {
      column(name: 'column-name',
             type: 'varchar',
             value: 'value',
             defaultValue: 'default-string-value',
             autoIncrement: true,
             remarks: 'No comment')
    }

    def columnDelegate = new ColumnDelegate()
    closure.delegate = columnDelegate
    closure.call()

    def columns = columnDelegate.columns
    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig

    assertEquals 'column-name', column.name
    assertEquals 'varchar', column.type
    assertEquals 'value', column.value
    assertEquals 'default-string-value', column.defaultValue
    assertTrue column.autoIncrement
    assertEquals 'No comment', column.remarks
  }


  @Test
  void buildSimpleNumericColumn() {
    def closure = {
      column(name: 'column-name',
             type: 'varchar',
             value: 'value',
             defaultValueNumeric: 42,
             autoIncrement: false,
             remarks: 'No numeric comment')
    }

    def columnDelegate = new ColumnDelegate()
    closure.delegate = columnDelegate
    closure.call()

    def columns = columnDelegate.columns
    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig

    assertEquals 'column-name', column.name
    assertEquals 'varchar', column.type
    assertEquals 'value', column.value
    assertEquals 42, column.defaultValueNumeric
    assertFalse column.autoIncrement
    assertEquals 'No numeric comment', column.remarks
  }


  @Test
  void buildSimpleDateColumn() {
    def now = "2010-11-02 06:52:04"
    def sqlNow = new Timestamp(1288702324000)
    def closure = {
      column(name: 'column-name',
             type: 'datetime',
             value: 'value',
             defaultValueDate: now,
             autoIncrement: false,
             remarks: 'No date comment')
    }

    def columnDelegate = new ColumnDelegate()
    closure.delegate = columnDelegate
    closure.call()

    def columns = columnDelegate.columns
    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig

    assertEquals 'column-name', column.name
    assertEquals 'datetime', column.type
    assertEquals 'value', column.value
    assertEquals sqlNow, column.defaultValueDate
    assertFalse column.autoIncrement
    assertEquals 'No date comment', column.remarks
  }


  @Test
  void buildSimpleBooleanColumn() {
    def closure = {
      column(name: 'column-name',
             type: 'bit',
             value: 'value',
             defaultValueBoolean: true,
             autoIncrement: false,
             remarks: 'No boolean comment')
    }

    def columnDelegate = new ColumnDelegate()
    closure.delegate = columnDelegate
    closure.call()

    def columns = columnDelegate.columns
    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig

    assertEquals 'column-name', column.name
    assertEquals 'bit', column.type
    assertEquals 'value', column.value
    assertTrue column.defaultValueBoolean
    assertFalse column.autoIncrement
    assertEquals 'No boolean comment', column.remarks
  }


  @Test
  void buildStringColumnWithConstraintsInMap() {
    def closure = {
      column(name: 'column-name',
             type: 'varchar',
             value: 'value',
             defaultValue: 'default-string-value',
             autoIncrement: true,
             remarks: 'No comment') {
        constraints(nullable: false, unique: true)
      }
    }

    def columnDelegate = new ColumnDelegate()
    closure.delegate = columnDelegate
    closure.call()

    def columns = columnDelegate.columns
    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig

    assertEquals 'column-name', column.name
    assertEquals 'varchar', column.type
    assertEquals 'value', column.value
    assertEquals 'default-string-value', column.defaultValue
    assertTrue column.autoIncrement
    assertEquals 'No comment', column.remarks

    def constraints = column.constraints
    assertNotNull constraints
    assertTrue constraints instanceof ConstraintsConfig
    assertTrue constraints.isUnique()
    assertFalse constraints.isNullable()
  }


  @Test
  void buildStringColumnWithConstraintsInClosure() {
    def closure = {
      column(name: 'column-name',
             type: 'varchar',
             value: 'value',
             defaultValue: 'default-string-value',
             autoIncrement: true,
             remarks: 'No comment') {
        constraints {
          nullable(false)
          unique(true)
        }
      }
    }

    def columnDelegate = new ColumnDelegate()
    closure.delegate = columnDelegate
    closure.call()

    def columns = columnDelegate.columns
    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig

    assertEquals 'column-name', column.name
    assertEquals 'varchar', column.type
    assertEquals 'value', column.value
    assertEquals 'default-string-value', column.defaultValue
    assertTrue column.autoIncrement
    assertEquals 'No comment', column.remarks

    def constraints = column.constraints
    assertNotNull constraints
    assertTrue constraints instanceof ConstraintsConfig
    assertTrue constraints.isUnique()
    assertFalse constraints.isNullable()
  }


  @Test
  void buildMultipleColumns() {
    def closure = {
      column(name: 'column-1', type: 'varchar', value: 'value')
      column(name: 'column-2', type: 'integer', valueNumeric: 42)
      column(name: 'column-3', type: 'boolean', valueBoolean: true)
    }

    def columnDelegate = new ColumnDelegate()
    closure.delegate = columnDelegate
    closure.call()

    def columns = columnDelegate.columns
    assertNotNull columns
    assertEquals 3, columns.size()
    assertTrue columns[0] instanceof ColumnConfig
    assertTrue columns[1] instanceof ColumnConfig
    assertTrue columns[2] instanceof ColumnConfig

    assertEquals 'column-1', columns[0].name
    assertEquals 'varchar', columns[0].type
    assertEquals 'value', columns[0].value
    assertEquals 'column-2', columns[1].name
    assertEquals 'integer', columns[1].type
    assertEquals 42, columns[1].valueNumeric
    assertEquals 'column-3', columns[2].name
    assertEquals 'boolean', columns[2].type
    assertTrue columns[2].valueBoolean
  }

}

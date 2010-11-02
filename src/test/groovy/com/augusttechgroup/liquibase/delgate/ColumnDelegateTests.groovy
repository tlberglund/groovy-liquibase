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

import com.augusttechgroup.liquibase.GroovyLiquibaseChangeLogParser

import liquibase.parser.ChangeLogParserFactory
import liquibase.resource.FileSystemResourceAccessor
import liquibase.changelog.ChangeSet
import liquibase.change.core.AddColumnChange
import liquibase.change.ColumnConfig

import org.junit.Test
import org.junit.Before
import static org.junit.Assert.*
import java.sql.Timestamp


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

}

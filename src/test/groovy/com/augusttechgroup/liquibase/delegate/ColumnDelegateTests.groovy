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

import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression.ClosureContext;

import liquibase.change.ColumnConfig

import org.junit.Test
import static org.junit.Assert.*
import java.sql.Timestamp
import liquibase.change.ConstraintsConfig
import liquibase.change.core.LoadDataColumnConfig
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.DatabaseChangeLog
import java.text.SimpleDateFormat


class ColumnDelegateTests
{
  def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  @Test
  void buildSimpleStringColumn() {
    def columns = buildColumns {
      column(name: 'column-name',
             type: 'varchar',
             value: 'value',
             defaultValue: 'default-string-value',
             remarks: 'No comment')
    }

    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig

    assertEquals 'column-name', column.name
    assertEquals 'varchar', column.type
    assertEquals 'value', column.value
    assertEquals 'default-string-value', column.defaultValue
    assertNull column.isAutoIncrement()
    assertEquals 'No comment', column.remarks
  }


  @Test
  void buildSimpleNumericColumn() {
    def columns = buildColumns {
      column(name: 'column-name',
             type: 'varchar',
             valueNumeric: 56,
             defaultValueNumeric: 42,
             autoIncrement: true,
             remarks: 'No numeric comment')
    }

    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig

    assertEquals 'column-name', column.name
    assertEquals 'varchar', column.type
    assertEquals 56, column.valueNumeric
    assertEquals 42, column.defaultValueNumeric
    assertTrue column.autoIncrement
    assertEquals 'No numeric comment', column.remarks
  }


  @Test
  void buildSimpleDateColumn() {
    def now = "2010-11-02 07:52:04"
    def sqlNow = parseSqlTimestamp(now)
    def columns = buildColumns {
      column(name: 'column-name',
             type: 'datetime',
             valueDate: now,
             defaultValueDate: now,
             autoIncrement: false,
             remarks: 'No date comment')
    }

    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig

    assertEquals 'column-name', column.name
    assertEquals 'datetime', column.type
    assertEquals sqlNow, column.valueDate
    assertEquals sqlNow, column.defaultValueDate
    assertFalse column.autoIncrement
    assertEquals 'No date comment', column.remarks
  }


  @Test
  void buildSimpleBooleanColumn() {
    def columns = buildColumns {
      column(name: 'column-name',
             type: 'bit',
             valueBoolean: true,
             defaultValueBoolean: true,
             autoIncrement: false,
             remarks: 'No boolean comment')
    }

    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig

    assertEquals 'column-name', column.name
    assertEquals 'bit', column.type
    assertTrue column.valueBoolean
    assertTrue column.defaultValueBoolean
    assertFalse column.autoIncrement
    assertEquals 'No boolean comment', column.remarks
  }


  @Test
  void buildStringColumnWithConstraintsInMap() {
    def columns = buildColumns {
      column(name: 'column-name',
             type: 'varchar',
             value: 'value',
             defaultValue: 'default-string-value',
             autoIncrement: true,
             remarks: 'No comment') {
        constraints(nullable: false, unique: true)
      }
    }

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
    def columns = buildColumns {
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
    def columns = buildColumns {
      column(name: 'column-1', type: 'varchar', value: 'value')
      column(name: 'column-2', type: 'integer', valueNumeric: 42)
      column(name: 'column-3', type: 'boolean', valueBoolean: true)
    }

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


  @Test
  void buildloadDataColumnConfigColumnWithHeaders() {
    def columns = buildColumns(columnConfigClass: LoadDataColumnConfig) {
      column(header: 'header-name',
             name: 'database-column-name',
             type: 'STRING')
    }

    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof LoadDataColumnConfig

    assertEquals 'database-column-name', column.name
    assertEquals 'header-name', column.header
    assertEquals 'STRING', column.type
  }


  @Test
  void columnClosureCanContainWhereClause() {
    def columnDelegate = buildColumnDelegate([:]) {
      column(name: 'monkey', type: 'VARCHAR(50)')
      where "emotion='angry'"
    }
    
    def columns = columnDelegate.columns

    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof ColumnConfig
    assertEquals "emotion='angry'", columnDelegate.whereClause
  }


  @Test
  void buildloadDataColumnConfigColumnWithIndex() {
    def columns = buildColumns(columnConfigClass: LoadDataColumnConfig) {
      column(index: 3,
             name: 'database-column-name',
             type: 'STRING')
    }

    assertNotNull columns
    assertEquals 1, columns.size()
    def column = columns[0]
    assertTrue column instanceof LoadDataColumnConfig

    assertEquals 'database-column-name', column.name
    assertEquals 3, column.index
    assertEquals 'STRING', column.type
  }
  
  def buildColumnDelegate(Map args, Closure closure) {
      def changelog = new DatabaseChangeLog()
      changelog.changeLogParameters = new ChangeLogParameters()
      args.databaseChangeLog = changelog
      
      def columnDelegate = new ColumnDelegate(args)
      closure.delegate = columnDelegate
      closure.resolveStrategy = Closure.DELEGATE_FIRST
      closure.call()
      
      columnDelegate
  }
  
  def buildColumns(Map args, Closure closure) {
      buildColumnDelegate(args, closure).columns
  }
  
  def buildColumns(Closure closure) {
      buildColumns([:], closure)
  }


  private Timestamp parseSqlTimestamp(dateTimeString) {
    new Timestamp(sdf.parse(dateTimeString).time)
  }
}

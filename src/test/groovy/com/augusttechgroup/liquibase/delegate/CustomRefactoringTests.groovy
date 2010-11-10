//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

package com.augusttechgroup.liquibase.delegate

import org.junit.Test
import static org.junit.Assert.*
import liquibase.change.core.RawSQLChange
import liquibase.change.core.SQLFileChange
import liquibase.change.core.ExecuteShellCommandChange
import liquibase.change.custom.CustomChangeWrapper

class CustomRefactoringTests
  extends ChangeSetTests
{
  
  @Test
  void customSqlWithParameters() {
    buildChangeSet {
      sql(stripComments: true, splitStatements: true, endDelimiter: '!') {
        "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)"
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof RawSQLChange
    assertTrue changes[0].isStrippingComments()
    assertTrue changes[0].isSplittingStatements()
    assertEquals '!', changes[0].endDelimiter
    assertEquals "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)", changes[0].sql
  }


  @Test
  void customSqlWithoutParameters() {
    buildChangeSet {
      sql {
        "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)"
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof RawSQLChange
    assertEquals "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)", changes[0].sql
  }


  @Test
  void customSqlWithoutClosure() {
    buildChangeSet {
      sql "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)"
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof RawSQLChange
    assertEquals "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)", changes[0].sql
  }


  @Test
  void sqlFile() {
    buildChangeSet {
      sqlFile(path: 'db/file.sql', stripComments: true, splitStatements: true, encoding: 'UTF-8', endDelimiter: '@')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof SQLFileChange
    assertEquals 'db/file.sql', changes[0].path
    assertEquals 'UTF-8', changes[0].encoding
    assertTrue changes[0].isStrippingComments()
    assertTrue changes[0].isSplittingStatements()
    assertEquals '@', changes[0].endDelimiter
  }


  @Test
  void executeCommandWithNoArgs() {
    buildChangeSet {
      executeCommand(executable: "awk '/monkey/ { count++ } END { print count }'", os: 'Mac OS X, Linux')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof ExecuteShellCommandChange
    assertEquals "awk '/monkey/ { count++ } END { print count }'", changes[0].executable
    // There is no direct way to observe the 'os' property in ExecuteShellCommandChange
  }


  @Test
  void executeCommandWithArgs() {
    buildChangeSet {
      executeCommand(executable: "awk", os: 'Mac OS X, Linux') {
        arg('/monkey/ { count++ } END { print count }')
        arg('-f database.log')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof ExecuteShellCommandChange
    assertEquals "awk", changes[0].executable
    // There is no direct way to observe the 'os' property in ExecuteShellCommandChange
    def args = changes[0].args
    assertNotNull args
    assertEquals 2, args.size()
    assertTrue args.every { arg -> arg instanceof String }
    assertEquals '/monkey/ { count++ } END { print count }', args[0]
    assertEquals '-f database.log', args[1]
  }


  @Test
  void customRefactoringWithClassAndNoParameters() {
    buildChangeSet {
      customChange(class: 'org.liquibase.change.custom.MonkeyChange')
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof CustomChangeWrapper
    assertEquals 'org.liquibase.change.custom.MonkeyChange', changes[0].className
  }


  @Test
  void customRefactoringWithClassAndParameters() {
    buildChangeSet {
      customChange(class: 'org.liquibase.change.custom.MonkeyChange') {
        emotion('angry')
        'rfid-tag'(28763)
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof CustomChangeWrapper
    assertEquals 'org.liquibase.change.custom.MonkeyChange', changes[0].className
    def args = changes[0].paramValues
    assertNotNull args
    assertEquals 2, args.size()
    assertTrue args.containsKey('emotion')
    assertTrue args.containsKey('rfid-tag')
    assertEquals 'angry', args.emotion
    assertEquals '28763', args.'rfid-tag'
  }

}
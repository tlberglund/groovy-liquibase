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

import org.junit.Test
import static org.junit.Assert.*
import liquibase.change.core.RawSQLChange

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

}
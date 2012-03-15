package com.augusttechgroup.liquibase.delegate

import static org.junit.Assert.*
import liquibase.change.core.RawSQLChange

import org.junit.Test

import com.augusttechgroup.liquibase.change.CustomProgrammaticChangeWrapper
import com.augusttechgroup.liquibase.custom.MyCustomSqlChange


/**
 * Test the ability to extend the ChangeSetDelegate through groovy metaprogramming 
 *
 * @author Jason Clawson
 */
class DelegateExtensionTests 
  extends ChangeSetTests {

  @Test
  void testMyCustomSqlChange() {
    buildChangeSet {
      myCustomSqlChange()
    }

    def changes = changeSet.changes

    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof CustomProgrammaticChangeWrapper
    assertTrue changes[0].customChange instanceof MyCustomSqlChange
    assertEquals(new RawSQLChange("SELECT * FROM monkey").sql,
                 changes[0].customChange.generateStatements(null)[0].sql);
  }
}

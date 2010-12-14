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


/**
 * <p></p>
 * 
 * @author Tim Berglund
 */
class ChangeSetPreconditionTests
  extends ChangeSetTests
{

  @Test
  void testPreconditionWithoutParams() {
    buildChangeSet {
      preConditions {
        dbms(type: 'mysql')
      }
      addColumn(tableName: 'animal') {
        column(name: 'monkey_status', type: 'varchar(98)')
      }
    }

    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    def preconditions = changeSet.preconditions?.nestedPreconditions
    assertNotNull preconditions
  }
}
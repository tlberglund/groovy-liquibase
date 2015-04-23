/*
 * Copyright 2011-2014 Tim Berglund and Steven C. Saliman
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

package net.saliman.liquibase.delegate

import static org.junit.Assert.*
import liquibase.change.core.RawSQLChange

import org.junit.Test

import net.saliman.liquibase.change.CustomProgrammaticChangeWrapper
import net.saliman.liquibase.custom.MyCustomSqlChange


/**
 * Test the ability to extend the ChangeSetDelegate through groovy metaprogramming 
 *
 * @author Jason Clawson
 */
class DelegateExtensionTests extends ChangeSetTests {

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
	  assertNoOutput()
  }
}

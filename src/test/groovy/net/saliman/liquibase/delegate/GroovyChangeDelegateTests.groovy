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

import org.junit.Test
import org.junit.Ignore
import static org.junit.Assert.*
import liquibase.resource.FileSystemResourceAccessor

/**
 * <p></p>
 * 
 * @author Tim Berglund
 */
class GroovyChangeDelegateTests extends ChangeSetTests {

  @Ignore
  @Test
  void basicGroovyChangeSet() {

    def initWasCalled = false
    def validateWasCalled = false
    def changeWasCalled = false
    def rollbackWasCalled = false
    resourceAccessor = new FileSystemResourceAccessor()

    buildChangeSet {
      groovyChange {
        init {
          initWasCalled = true
        }
        validate {
          validateWasCalled = true
          assertNotNull changeSet
          assertNotNull resourceAccessor
          warn 'validation warning'
          error 'validation error'
        }
        change {
          changeWasCalled = true
          assertNotNull changeSet
          assertNotNull resourceAccessor
          assertNotNull database
          assertNotNull databaseConnection
          assertNotNull connection
          assertNotNull sql
          confirm 'change confirmed'
        }
        rollback {
          rollbackWasCalled = true
          assertNotNull changeSet
          assertNotNull resourceAccessor
          assertNotNull database
          assertNotNull databaseConnection
          assertNotNull connection
          assertNotNull sql
          confirm 'rollback confirmed'
        }
        assertNotNull changeSet
        assertNotNull resourceAccessor
        assertNotNull database
        assertNotNull databaseConnection
        assertNotNull connection
        assertNotNull sql
        confirm 'Basic GroovyChange executed'
        checksum 'd0763edaa9d9bdx`2a9516280e9044d885'
      }
    }

    assertTrue initWasCalled
    assertTrue validateWasCalled
    assertTrue changeWasCalled
    assertTrue rollbackWasCalled
	  assertNoOutput()

  }
}


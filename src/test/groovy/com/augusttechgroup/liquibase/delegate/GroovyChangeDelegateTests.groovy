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
import org.junit.Ignore
import static org.junit.Assert.*
import liquibase.resource.FileSystemResourceAccessor

/**
 * <p></p>
 * 
 * @author Tim Berglund
 */
class GroovyChangeDelegateTests
  extends ChangeSetTests
{

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
  }
}
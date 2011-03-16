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

    buildChangeSet {
      groovyChange {
        init {

        }
        validate {

        }
        change {

        }
        rollback {
          
        }
        confirm 'Basic GroovyChange executed'
        checksum 'd0763edaa9d9bd2a9516280e9044d885'
      }
    }


    


  }
}
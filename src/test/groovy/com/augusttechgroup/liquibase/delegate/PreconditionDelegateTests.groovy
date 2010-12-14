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
import liquibase.precondition.core.DBMSPrecondition
import liquibase.precondition.Precondition
import liquibase.precondition.core.RunningAsPrecondition
import liquibase.precondition.core.ChangeSetExecutedPrecondition
import liquibase.precondition.core.ColumnExistsPrecondition
import liquibase.precondition.core.TableExistsPrecondition
import liquibase.precondition.core.ViewExistsPrecondition
import liquibase.precondition.core.ForeignKeyExistsPrecondition
import liquibase.precondition.core.IndexExistsPrecondition
import liquibase.precondition.core.SequenceExistsPrecondition
import liquibase.precondition.core.PrimaryKeyExistsPrecondition
import liquibase.precondition.core.AndPrecondition
import liquibase.precondition.core.OrPrecondition
import liquibase.precondition.core.SqlPrecondition
import liquibase.precondition.CustomPreconditionWrapper


class PreconditionDelegateTests
{

  @Test
  void dbms() {
    def c = {
      dbms(type: 'mysql')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof DBMSPrecondition
    assertEquals 'mysql', preconditions[0].type
  }


  @Test
  void runningAs() {
    def c = {
      runningAs(username: 'tlberglund')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof RunningAsPrecondition
    assertEquals 'tlberglund', preconditions[0].username
  }


  @Test
  void changeSetExecuted() {
    def c = {
      changeSetExecuted(id: 'unleash-monkey', author: 'tlberglund', changeLogFile: 'changelog.xml')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof ChangeSetExecutedPrecondition
    assertEquals 'unleash-monkey', preconditions[0].id
    assertEquals 'tlberglund', preconditions[0].author
    assertEquals 'changelog.xml', preconditions[0].changeLogFile
  }


  @Test
  void columnExists() {
    def c = {
      columnExists(schemaName: 'schema', tableName: 'monkey', columnName: 'emotion')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof ColumnExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'monkey', preconditions[0].tableName
    assertEquals 'emotion', preconditions[0].columnName
  }


  @Test
  void tableExists() {
    def c = {
      tableExists(schemaName: 'schema', tableName: 'monkey')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof TableExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'monkey', preconditions[0].tableName
  }


  @Test
  void viewExists() {
    def c = {
      viewExists(schemaName: 'schema', viewName: 'monkey_view')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof ViewExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'monkey_view', preconditions[0].viewName
  }
  

  @Test
  void foreignKeyConstraintExists() {
    def c = {
      foreignKeyConstraintExists(schemaName: 'schema', foreignKeyName: 'fk_monkey_key')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof ForeignKeyExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'fk_monkey_key', preconditions[0].foreignKeyName
  }


  @Test
  void indexExists() {
    def c = {
      indexExists(schemaName: 'schema', indexName: 'index')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof IndexExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'index', preconditions[0].indexName
  }


  @Test
  void sequenceExists() {
    def c = {
      sequenceExists(schemaName: 'schema', sequenceName: 'seq_next_monkey')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof SequenceExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'seq_next_monkey', preconditions[0].sequenceName
  }


  @Test
  void primaryKeyExists() {
    def c = {
      primaryKeyExists(schemaName: 'schema', primaryKeyName: 'pk_monkey')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof PrimaryKeyExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'pk_monkey', preconditions[0].primaryKeyName
  }


  @Test
  void andClause() {
    def c = {
      and {
        dbms(type: 'mysql')
        runningAs(username: 'tlberglund')
      }
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof AndPrecondition
    def andedPreconditions = preconditions[0].nestedPreconditions
    assertNotNull andedPreconditions
    assertEquals 2, andedPreconditions.size()
    assertTrue andedPreconditions[0] instanceof DBMSPrecondition
    assertTrue andedPreconditions[1] instanceof RunningAsPrecondition
  }


  @Test
  void orClause() {
    def c = {
      or {
        dbms(type: 'mysql')
        runningAs(username: 'tlberglund')
      }
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof OrPrecondition
    def andedPreconditions = preconditions[0].nestedPreconditions
    assertNotNull andedPreconditions
    assertEquals 2, andedPreconditions.size()
    assertTrue andedPreconditions[0] instanceof DBMSPrecondition
    assertTrue andedPreconditions[1] instanceof RunningAsPrecondition
  }


  @Test
  void sqlCheck() {
    def c = {
      sqlCheck(expectedResult: 'angry') {
        "SELECT emotion FROM monkey WHERE id=2884"
      }
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof SqlPrecondition
    assertEquals 'angry', preconditions[0].expectedResult
    assertEquals 'SELECT emotion FROM monkey WHERE id=2884', preconditions[0].sql
  }



  @Test
  void customPreconditionFails() {
    def c = {
      customPrecondition(className: 'org.liquibase.precondition.MonkeyFailPrecondition') {
        emotion('angry')
        'rfid-tag'(28763)
      }
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()

    def preconditions = delegate.preconditions
    assertNotNull preconditions
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof CustomPreconditionWrapper
    // There is no way to examine these parameters once they are set in a CustomPreconditionWrapper
    //assertEquals 'angry', preconditions[0].expectedResult
    //assertEquals 'SELECT emotion FROM monkey WHERE id=2884', preconditions[0].sql
  }
}
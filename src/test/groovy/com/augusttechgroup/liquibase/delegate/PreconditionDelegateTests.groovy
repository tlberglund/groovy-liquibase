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
import liquibase.precondition.core.PreconditionContainer
import liquibase.precondition.core.PreconditionContainer.FailOption
import liquibase.precondition.core.PreconditionContainer.ErrorOption
import liquibase.precondition.core.PreconditionContainer.OnSqlOutputOption
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


class PreconditionDelegateTests
{

  @Test
  void preconditionParameterOptions() {
    def params = [onFail: 'WARN', onError: 'HALT', onUpdateSQL: 'IGNORE', onFailMessage: 'fail-message!!!1!!1one!', onErrorMessage: 'error-message']
    def delegate = new PreconditionDelegate(params)
    def preconditions = delegate.preconditions

    assertNotNull preconditions
    assertTrue preconditions instanceof PreconditionContainer
    assertEquals FailOption.WARN, preconditions.onFail
    assertEquals ErrorOption.HALT, preconditions.onError
    assertEquals OnSqlOutputOption.IGNORE, preconditions.onSqlOutput
    assertEquals 'fail-message!!!1!!1one!', preconditions.onFailMessage
    assertEquals 'error-message', preconditions.onErrorMessage
  }


  @Test
  void dbms() {
    def c = {
      dbms(type: 'mysql')
    }

    def delegate = new PreconditionDelegate()
    c.delegate = delegate
    c.call()
    def container = delegate.preconditions

    assertNotNull container
    assertTrue container instanceof PreconditionContainer
    def preconditions = container.nestedPreconditions
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
    def container = delegate.preconditions

    assertNotNull container
    assertTrue container instanceof PreconditionContainer
    def preconditions = container.nestedPreconditions
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
    def container = delegate.preconditions

    assertNotNull container
    assertTrue container instanceof PreconditionContainer
    def preconditions = container.nestedPreconditions
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
    def container = delegate.preconditions

    assertNotNull container
    assertTrue container instanceof PreconditionContainer
    def preconditions = container.nestedPreconditions
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
    def container = delegate.preconditions

    assertNotNull container
    assertTrue container instanceof PreconditionContainer
    def preconditions = container.nestedPreconditions
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
    def container = delegate.preconditions

    assertNotNull container
    assertTrue container instanceof PreconditionContainer
    def preconditions = container.nestedPreconditions
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
    def container = delegate.preconditions

    assertNotNull container
    assertTrue container instanceof PreconditionContainer
    def preconditions = container.nestedPreconditions
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
    def container = delegate.preconditions

    assertNotNull container
    assertTrue container instanceof PreconditionContainer
    def preconditions = container.nestedPreconditions
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
    def container = delegate.preconditions

    assertNotNull container
    assertTrue container instanceof PreconditionContainer
    def preconditions = container.nestedPreconditions
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
    def container = delegate.preconditions

    assertNotNull container
    assertTrue container instanceof PreconditionContainer
    def preconditions = container.nestedPreconditions
    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof PrimaryKeyExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'pk_monkey', preconditions[0].primaryKeyName
  }


  @Test
  void andClause() {
    def closure = {
      and {
        
      }
    }
  }

}
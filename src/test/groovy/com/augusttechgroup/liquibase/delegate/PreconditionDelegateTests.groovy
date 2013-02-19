/*
 * Copyright 2011 Tim Berglund
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

package com.augusttechgroup.liquibase.delegate

import groovy.lang.Closure;

import org.junit.Test
import static org.junit.Assert.*
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.DatabaseChangeLog
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
    def preconditions = buildPreconditions {
      dbms(type: 'mysql')
    }

    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof DBMSPrecondition
    assertEquals 'mysql', preconditions[0].type
  }


  @Test
  void runningAs() {
    def preconditions = buildPreconditions {
      runningAs(username: 'tlberglund')
    }

    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof RunningAsPrecondition
    assertEquals 'tlberglund', preconditions[0].username
  }


  @Test
  void changeSetExecuted() {
    def preconditions = buildPreconditions {
      changeSetExecuted(id: 'unleash-monkey', author: 'tlberglund', changeLogFile: 'changelog.xml')
    }

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
    def preconditions = buildPreconditions {
      columnExists(schemaName: 'schema', tableName: 'monkey', columnName: 'emotion')
    }

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
    def preconditions = buildPreconditions {
      tableExists(schemaName: 'schema', tableName: 'monkey')
    }

    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof TableExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'monkey', preconditions[0].tableName
  }


  @Test
  void viewExists() {
    def preconditions = buildPreconditions {
      viewExists(schemaName: 'schema', viewName: 'monkey_view')
    }

    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof ViewExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'monkey_view', preconditions[0].viewName
  }
  

  @Test
  void foreignKeyConstraintExists() {
    def preconditions = buildPreconditions {
      foreignKeyConstraintExists(schemaName: 'schema', foreignKeyName: 'fk_monkey_key')
    }

    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof ForeignKeyExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'fk_monkey_key', preconditions[0].foreignKeyName
  }


  @Test
  void indexExists() {
    def preconditions = buildPreconditions {
      indexExists(schemaName: 'schema', indexName: 'index')
    }

    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof IndexExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'index', preconditions[0].indexName
  }


  @Test
  void sequenceExists() {
    def preconditions = buildPreconditions {
      sequenceExists(schemaName: 'schema', sequenceName: 'seq_next_monkey')
    }

    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof SequenceExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'seq_next_monkey', preconditions[0].sequenceName
  }


  @Test
  void primaryKeyExists() {
    def preconditions = buildPreconditions {
      primaryKeyExists(schemaName: 'schema', primaryKeyName: 'pk_monkey')
    }

    assertNotNull preconditions
    assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof PrimaryKeyExistsPrecondition
    assertEquals 'schema', preconditions[0].schemaName
    assertEquals 'pk_monkey', preconditions[0].primaryKeyName
  }


  @Test
  void andClause() {
    def preconditions = buildPreconditions {
      and {
        dbms(type: 'mysql')
        runningAs(username: 'tlberglund')
      }
    }

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
    def preconditions = buildPreconditions {
      or {
        dbms(type: 'mysql')
        runningAs(username: 'tlberglund')
      }
    }

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
    def preconditions = buildPreconditions {
      sqlCheck(expectedResult: 'angry') {
        "SELECT emotion FROM monkey WHERE id=2884"
      }
    }

    assertNotNull preconditions
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof SqlPrecondition
    assertEquals 'angry', preconditions[0].expectedResult
    assertEquals 'SELECT emotion FROM monkey WHERE id=2884', preconditions[0].sql
  }



  @Test
  void customPreconditionFails() {
    def preconditions = buildPreconditions {
      customPrecondition(className: 'org.liquibase.precondition.MonkeyFailPrecondition') {
        emotion('angry')
        'rfid-tag'(28763)
      }
    }

    assertNotNull preconditions
    assertEquals 1, preconditions.size()
    assertTrue preconditions[0] instanceof CustomPreconditionWrapper
    // There is no way to examine these parameters once they are set in a CustomPreconditionWrapper
    //assertEquals 'angry', preconditions[0].expectedResult
    //assertEquals 'SELECT emotion FROM monkey WHERE id=2884', preconditions[0].sql
  }
  
  
  
  def buildPreconditions(Closure closure) {
      def changelog = new DatabaseChangeLog()
      changelog.changeLogParameters = new ChangeLogParameters()
      
      def delegate = new PreconditionDelegate(databaseChangeLog: changelog)
      closure.delegate = delegate
      closure.resolveStrategy = Closure.DELEGATE_FIRST
      closure.call()
      
      delegate.preconditions
  }
}
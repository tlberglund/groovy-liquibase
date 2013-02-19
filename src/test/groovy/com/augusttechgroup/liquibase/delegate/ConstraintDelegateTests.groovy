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

import org.junit.Test
import static org.junit.Assert.*

import liquibase.change.ConstraintsConfig
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.DatabaseChangeLog


class ConstraintDelegateTests
{


  @Test
  void verifyDefaultConstraints() {
    def constraint = buildConstraint {
      constraints()
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.isNullable()
    assertFalse constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }


  @Test
  void primaryKeyConstraintFromMapWithMultipleCalls() {
    def constraint = buildConstraint {
      constraints(nullable: false)
      constraints(primaryKey: true)
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.isNullable()
    assertTrue constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }


  @Test
  void primaryKeyConstraintFromClosureWithMultipleCalls() {
    def constraint = buildConstraint {
      constraints {
        nullable(false)
      }
      constraints {
        primaryKey(true)
      }
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.isNullable()
    assertTrue constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }



  @Test
  void simplePrimaryKeyConstraintFromMap() {
    def constraint = buildConstraint {
      constraints(nullable: false, primaryKey: true)
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.isNullable()
    assertTrue constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }


  @Test
  void simplePrimaryKeyConstraintFromClosure() {
    def constraint = buildConstraint {
      constraints {
        nullable(false)
        primaryKey(true)
      }
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.isNullable()
    assertTrue constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }


  @Test
  void richPrimaryKeyConstraintFromMap() {
    def constraint = buildConstraint {
      constraints(nullable: false, primaryKey: true, primaryKeyName: 'primary_key', primaryKeyTablespace: 'key_tablespace')
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.isNullable()
    assertTrue constraint.isPrimaryKey()
    assertEquals 'primary_key', constraint.primaryKeyName
    assertEquals 'key_tablespace', constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }


  @Test
  void richPrimaryKeyConstraintFromClosure() {
    def constraint = buildConstraint {
      constraints {
        nullable(false)
        primaryKey(true)
        primaryKeyName('primary_key')
        primaryKeyTablespace('key_tablespace')
      }
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.isNullable()
    assertTrue constraint.isPrimaryKey()
    assertEquals 'primary_key', constraint.primaryKeyName
    assertEquals 'key_tablespace', constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }


  @Test
  void foreignKeyConstraintFromMap() {
    def constraint = buildConstraint {
      constraints(nullable: true, foreignKeyName: 'foreign_key', references: 'monkey(id)', deleteCascade: true, deferrable: true, initiallyDeferred: true)
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.isNullable()
    assertFalse constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertEquals 'monkey(id)', constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertTrue constraint.isDeleteCascade()
    assertEquals 'foreign_key', constraint.foreignKeyName
    assertTrue constraint.isInitiallyDeferred()
    assertTrue constraint.isDeferrable()
  }


  @Test
  void foreignKeyConstraintFromClosure() {
    def constraint = buildConstraint {
      constraints {
        nullable(true)
        foreignKeyName('foreign_key')
        references('monkey(id)')
        deleteCascade(true)
        deferrable(true)
        initiallyDeferred(true)
      }
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.isNullable()
    assertFalse constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertEquals 'monkey(id)', constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertTrue constraint.isDeleteCascade()
    assertEquals 'foreign_key', constraint.foreignKeyName
    assertTrue constraint.isInitiallyDeferred()
    assertTrue constraint.isDeferrable()
  }


  @Test
  void uniqueConstraintFromMap() {
    def constraint = buildConstraint {
      constraints(unique: true, uniqueConstraintName: 'unique_column', check: 'check')
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.isNullable()
    assertFalse constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertTrue constraint.isUnique()
    assertEquals 'unique_column', constraint.uniqueConstraintName
    assertEquals 'check', constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }


  @Test
  void uniqueConstraintFromClosure() {
    def constraint = buildConstraint {
      constraints {
        unique(true)
        uniqueConstraintName('unique_column')
        check('check')
      }
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.isNullable()
    assertFalse constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertTrue constraint.isUnique()
    assertEquals 'unique_column', constraint.uniqueConstraintName
    assertEquals 'check', constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }


  @Test
  void simpleNullableConstraintFromMap() {
    def constraint = buildConstraint {
      constraints(nullable: true)
    }

    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.isNullable()
    assertFalse constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }


  @Test
  void simpleNullableConstraintFromClosure() {
    def constraint = buildConstraint {
      constraints {
        nullable(true)
      }
    }
    
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.isNullable()
    assertFalse constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.isDeleteCascade()
    assertNull constraint.foreignKeyName
    assertFalse constraint.isInitiallyDeferred()
    assertFalse constraint.isDeferrable()
  }
  
  def buildConstraint(Closure closure) {
      def changelog = new DatabaseChangeLog()
      changelog.changeLogParameters = new ChangeLogParameters()
      
      def delegate = new ConstraintDelegate(databaseChangeLog: changelog)
      closure.delegate = delegate
      closure.resolveStrategy = Closure.DELEGATE_FIRST
      closure.call()
      
      delegate.constraint
  }
}

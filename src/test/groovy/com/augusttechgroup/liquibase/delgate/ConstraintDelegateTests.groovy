//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the GNU Lesser General Public License v2.1
//

package com.augusttechgroup.liquibase.delegate

import org.junit.Test
import static org.junit.Assert.*

import liquibase.change.ConstraintsConfig
import org.junit.Ignore

class ConstraintDelegateTests
{

  @Test
  void primaryKeyConstraintFromMapWithMultipleCalls() {
    def closure = {
      constraint(nullable: false)
      constraint(primaryKey: true)
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.nullable
    assertTrue constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.deleteCascade
    assertNull constraint.foreignKeyName
    assertFalse constraint.initiallyDeferred
    assertFalse constraint.isDeferrable()
  }


  @Test
  void primaryKeyConstraintFromClosureWithMultipleCalls() {
    def closure = {
      constraint {
        nullable(false)
      }
      constraint {
        primaryKey(true)
      }
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.nullable
    assertTrue constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.deleteCascade
    assertNull constraint.foreignKeyName
    assertFalse constraint.initiallyDeferred
    assertFalse constraint.isDeferrable()
  }



  @Test
  void simplePrimaryKeyConstraintFromMap() {
    def closure = {
      constraint(nullable: false, primaryKey: true)
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.nullable
    assertTrue constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.deleteCascade
    assertNull constraint.foreignKeyName
    assertFalse constraint.initiallyDeferred
    assertFalse constraint.isDeferrable()
  }


  @Test
  void simplePrimaryKeyConstraintFromClosure() {
    def closure = {
      constraint {
        nullable(false)
        primaryKey(true)
      }
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
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
    assertFalse constraint.deleteCascade
    assertNull constraint.foreignKeyName
    assertFalse constraint.initiallyDeferred
    assertFalse constraint.isDeferrable()
  }


  @Test
  void richPrimaryKeyConstraintFromMap() {
    def closure = {
      constraint(nullable: false, primaryKey: true, primaryKeyName: 'primary_key', primaryKeyTablespace: 'key_tablespace')
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.nullable
    assertTrue constraint.isPrimaryKey()
    assertEquals 'primary_key', constraint.primaryKeyName
    assertEquals 'key_tablespace', constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.deleteCascade
    assertNull constraint.foreignKeyName
    assertFalse constraint.initiallyDeferred
    assertFalse constraint.isDeferrable()
  }


  @Test
  void richPrimaryKeyConstraintFromClosure() {
    def closure = {
      constraint {
        nullable(false)
        primaryKey(true)
        primaryKeyName('primary_key')
        primaryKeyTablespace('key_tablespace')
      }
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.nullable
    assertTrue constraint.isPrimaryKey()
    assertEquals 'primary_key', constraint.primaryKeyName
    assertEquals 'key_tablespace', constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.deleteCascade
    assertNull constraint.foreignKeyName
    assertFalse constraint.initiallyDeferred
    assertFalse constraint.isDeferrable()
  }


  @Test
  void foreignKeyConstraintFromMap() {
    def closure = {
      constraint(nullable: true, foreignKeyName: 'foreign_key', references: 'monkey(id)', deleteCascade: true, deferrable: true, initiallyDeferred: true)
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.nullable
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
    def closure = {
      constraint {
        nullable(true)
        foreignKeyName('foreign_key')
        references('monkey(id)')
        deleteCascade(true)
        deferrable(true)
        initiallyDeferred(true)
      }
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.nullable
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
    def closure = {
      constraint(unique: true, uniqueConstraintName: 'unique_column', check: 'check')
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.isNullable()
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
    def closure = {
      constraint {
        unique(true)
        uniqueConstraintName('unique_column')
        check('check')
      }
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertFalse constraint.isNullable()
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
    def closure = {
      constraint(nullable: true)
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.nullable
    assertFalse constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.deleteCascade
    assertNull constraint.foreignKeyName
    assertFalse constraint.initiallyDeferred
    assertFalse constraint.isDeferrable()
  }


  @Test
  void simpleNullableConstraintFromClosure() {
    def closure = {
      constraint {
        nullable(true)
      }
    }

    def delegate = new ConstraintDelegate()
    closure.delegate = delegate
    closure.call()

    def constraint = delegate.constraint
    assertNotNull constraint
    assertTrue constraint instanceof ConstraintsConfig
    assertTrue constraint.nullable
    assertFalse constraint.isPrimaryKey()
    assertNull constraint.primaryKeyName
    assertNull constraint.primaryKeyTablespace
    assertNull constraint.references
    assertFalse constraint.isUnique()
    assertNull constraint.uniqueConstraintName
    assertNull constraint.check
    assertFalse constraint.deleteCascade
    assertNull constraint.foreignKeyName
    assertFalse constraint.initiallyDeferred
    assertFalse constraint.isDeferrable()
  }


}

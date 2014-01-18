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

import liquibase.exception.ChangeLogParseException
import org.junit.After
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

import liquibase.change.ConstraintsConfig
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.DatabaseChangeLog

/**
 * This class tests that a constraint closure can be parsed correctly by the
 * Groovy DSL.  As with the other tests, we are not interested in whether or
 * not we end up with a valid constraint, just whether or not we faithfully
 * created a Liquibase object from the Groovy closure we were given.  We
 * defer to Liquibase on matters of validity.  Once again, Liquibase has
 * some options that are not documented, so we'll test them.
 *
 * @author Steven C. Saliman
 */
class ConstraintDelegateTests {
	def oldStdOut = System.out;
	def bufStr = new ByteArrayOutputStream()
	/**
	 * Set up for each test by and capturing standard out so that tests can check
	 * for the presence/absence of messages.
	 */
	@Before
	void captureStdOut() {
		System.out = new PrintStream(bufStr)
	}

	/**
	 * After each test, make sure stdout is back to what it should be, and for
	 * good measure, print out any output we got.
	 */
	@After
	void restoreStdOut() {
		if ( oldStdOut != null ) {
			System.out = oldStdOut
		}
		String testOutput = bufStr.toString()
		if ( testOutput != null && testOutput.length() > 0 ) {
			println("Test output:\n${testOutput}")
		}
	}

	/**
	 * Test parsing constraints when we have no args and no closure.  This
	 * validates that we don't set any unintended defaults.
	 */
	@Test
	void constraintWithoutArgsOrClosure() {
		def constraint = buildConstraint {
			constraints([:])
		}

		assertNotNull constraint
		assertTrue constraint instanceof ConstraintsConfig
		assertNull constraint.nullable
		assertNull constraint.primaryKey
		assertNull constraint.primaryKeyName
		assertNull constraint.primaryKeyTablespace
		assertNull constraint.foreignKeyName
		assertNull constraint.references
		assertNull constraint.referencedTableName
		assertNull constraint.referencedColumnNames
		assertNull constraint.unique
		assertNull constraint.uniqueConstraintName
		assertNull constraint.checkConstraint
		assertNull constraint.deleteCascade
		assertNull constraint.initiallyDeferred
		assertNull constraint.deferrable

		assertNoOutput()
	}

	/**
	 * Test parsing constraints when we have no args but we have a closure, which
	 * is empty. This should produce a parse exception, since this has been
	 * deprecated.
	 */
	@Test(expected = ChangeLogParseException)
	void constraintNoArgsEmptyClosure() {
		buildConstraint {
			constraints {}
		}
	}

	/**
	 * Test parsing constraints when we set all the attributes via the argument
	 * map.  We have too many booleans to isolate them, so we'll need some more
	 * tests there...
	 */
	@Test
	void fullArgumentsNoClosure() {
		def constraint = buildConstraint {
			constraints(nullable: true,
			            primaryKey: true,
			            primaryKeyName: 'myPrimaryKey',
			            primaryKeyTablespace: 'myPrimaryTablespace',
							    foreignKeyName: 'fk_monkey',
			            references: 'monkey(id)',
			            referencedTableName: 'monkey',
			            referencedColumnNames: 'id',
			            unique: true,
			            uniqueConstraintName: 'myUniqueKey',
			            checkConstraint: 'myCheckConstraint',
			            deleteCascade: true,
							    initiallyDeferred: true,
							    deferrable: true)
		}

		assertNotNull constraint
		assertTrue constraint instanceof ConstraintsConfig
		assertTrue constraint.nullable
		assertTrue constraint.primaryKey
		assertEquals 'myPrimaryKey', constraint.primaryKeyName
		assertEquals 'myPrimaryTablespace', constraint.primaryKeyTablespace
		assertEquals 'fk_monkey', constraint.foreignKeyName
		assertEquals 'monkey(id)', constraint.references
		assertEquals 'monkey', constraint.referencedTableName
		assertEquals 'id', constraint.referencedColumnNames
		assertTrue constraint.unique
		assertEquals 'myUniqueKey', constraint.uniqueConstraintName
		assertEquals 'myCheckConstraint', constraint.checkConstraint
		assertTrue constraint.deleteCascade
		assertTrue constraint.initiallyDeferred
		assertTrue constraint.deferrable

		assertNoOutput()
	}

	/**
	 * Set all the boolean attributes via arguments, but only "nullable" is true.
	 */
	@Test
	void onlyNullableArgIsTrue() {
		def constraint = buildConstraint {
			constraints(nullable: true,
						    	primaryKey: false,
							    unique: false,
							    deleteCascade: false,
							    initiallyDeferred: false,
							    deferrable: false)
		}

		assertNotNull constraint
		assertTrue constraint instanceof ConstraintsConfig
		assertTrue constraint.nullable
		assertFalse constraint.primaryKey
		assertFalse constraint.unique
		assertFalse constraint.deleteCascade
		assertFalse constraint.initiallyDeferred
		assertFalse constraint.deferrable

		assertNoOutput()
	}

	/**
	 * Set all the boolean attributes via arguments, but only "primaryKey" is true.
	 */
	@Test
	void onlyPrimaryKeyArgIsTrue() {
		def constraint = buildConstraint {
			constraints(nullable: false,
							    primaryKey: true,
							    unique: false,
							    deleteCascade: false,
							    initiallyDeferred: false,
							    deferrable: false)
		}

		assertNotNull constraint
		assertTrue constraint instanceof ConstraintsConfig
		assertFalse constraint.nullable
		assertTrue constraint.primaryKey
		assertFalse constraint.unique
		assertFalse constraint.deleteCascade
		assertFalse constraint.initiallyDeferred
		assertFalse constraint.deferrable

		assertNoOutput()
	}

	/**
	 * Set all the boolean attributes via arguments, but only "unique" is true.
	 */
	@Test
	void onlyUniqueArgIsTrue() {
		def constraint = buildConstraint {
			constraints(nullable: false,
							    primaryKey: false,
							    unique: true,
							    deleteCascade: false,
							    initiallyDeferred: false,
							    deferrable: false)
		}

		assertNotNull constraint
		assertTrue constraint instanceof ConstraintsConfig
		assertFalse constraint.nullable
		assertFalse constraint.primaryKey
		assertTrue constraint.unique
		assertFalse constraint.deleteCascade
		assertFalse constraint.initiallyDeferred
		assertFalse constraint.deferrable

		assertNoOutput()
	}

	/**
	 * Set all the boolean attributes via arguments, but only "deleteCascade" is true.
	 */
	@Test
	void onlyDeleteCascadeArgIsTrue() {
		def constraint = buildConstraint {
			constraints(nullable: false,
							    primaryKey: false,
							    unique: false,
							    deleteCascade: true,
							    initiallyDeferred: false,
							    deferrable: false)
		}

		assertNotNull constraint
		assertTrue constraint instanceof ConstraintsConfig
		assertFalse constraint.nullable
		assertFalse constraint.primaryKey
		assertFalse constraint.unique
		assertTrue constraint.deleteCascade
		assertFalse constraint.initiallyDeferred
		assertFalse constraint.deferrable

		assertNoOutput()
	}

	/**
	 * Set all the boolean attributes via arguments, but only "initiallyDeferred"
	 * is true.
	 */
	@Test
	void onlyInitiallyDeferredArgIsTrue() {
		def constraint = buildConstraint {
			constraints(nullable: false,
							    primaryKey: false,
						    	unique: false,
							    deleteCascade: false,
							    initiallyDeferred: true,
							    deferrable: false)
		}

		assertNotNull constraint
		assertTrue constraint instanceof ConstraintsConfig
		assertFalse constraint.nullable
		assertFalse constraint.primaryKey
		assertFalse constraint.unique
		assertFalse constraint.deleteCascade
		assertTrue constraint.initiallyDeferred
		assertFalse constraint.deferrable

		assertNoOutput()
	}

	/**
	 * Set all the boolean attributes via arguments, but only "deferrable" is true.
	 */
	@Test
	void onlyDeferrableArgIsTrue() {
		def constraint = buildConstraint {
			constraints(nullable: false,
							    primaryKey: false,
							    unique: false,
							    deleteCascade: false,
							    initiallyDeferred: false,
							    deferrable: true)
		}

		assertNotNull constraint
		assertTrue constraint instanceof ConstraintsConfig
		assertFalse constraint.nullable
		assertFalse constraint.primaryKey
		assertFalse constraint.unique
		assertFalse constraint.deleteCascade
		assertFalse constraint.initiallyDeferred
		assertTrue constraint.deferrable

		assertNoOutput()
	}

	/**
	 * Test parsing constraints in a closure was removed.  Make sure we get a
	 * parse exception.
	 */
	@Test(expected = ChangeLogParseException)
	void fullArgumentsInClosure() {
		buildConstraint {
			constraints {
				nullable(true)
				primaryKey(true)
				primaryKeyName('myPrimaryKey')
				primaryKeyTablespace('myPrimaryTablespace')
				foreignKeyName('fk_monkey')
				references('monkey(id)')
				referencedTableName('monkey')
				referencedColumnNames('id')
				unique(true)
				uniqueConstraintName('myUniqueKey')
				checkConstraint('myCheckConstraint')
				deleteCascade(true)
				initiallyDeferred(true)
				deferrable(true)
			}
		}
	}

	/**
	 * Constraints have an interesting wrinkle.  Where multiple calls to "column"
	 * results in multiple columns being created, multiple calls to "constraint"
	 * results in just one, combined constraint.  This is useful if a column
	 * has both a unique constraint and a foreign key constraint.  Test this.
	 */
	@Test
	void constraintsFromMapWithMultipleCalls() {
		def constraint = buildConstraint {
			constraints(foreignKeyName: 'fk_monkey', references: 'monkey(id)')
			constraints(unique: true, uniqueConstraintName: 'uk_monkey')
		}

		assertNotNull constraint
		assertTrue constraint instanceof ConstraintsConfig
		assertEquals 'fk_monkey', constraint.foreignKeyName
		assertEquals 'monkey(id)', constraint.references
		assertTrue constraint.unique
		assertEquals 'uk_monkey', constraint.uniqueConstraintName

		assertNoOutput()
	}

	/**
	 * Try calling a constraints closure with an invalid attribute in the map.
	 * Expect our ChangeLogParseException with a good message instead of
	 * Liquibase's RuntimeException
	 */
	@Test(expected = ChangeLogParseException)
	void constraintHasInvalidAttribute() {
		buildConstraint {
			constraints(someAttr: 'someValue')
		}
	}

	/**
	 * Try calling a constraints closure with an invalid method in the nested
	 * closure. Expect our ChangeLogParseException with a good message instead of
	 * Liquibase's RuntimeException
	 */
	@Test(expected = ChangeLogParseException)
	void constraintHasInvalidMethod() {
		buildConstraint {
			constraints{
				someAttr('value')
			}
		}
	}

	/**
	 * Make sure the test did not have any output to standard out.  This can be
	 * used to make sure there are no deprecation warnings.
	 */
	void assertNoOutput() {
		String testOutput = bufStr.toString()
		assertTrue "Did not expect to have output, but got:\n '${testOutput}",
						testOutput.length() < 1
	}

	/**
	 * Helper method to execute a constraint closure and return the constraint
	 * created from it.
	 * @param closure the closure to execute
	 * @return the closure object built.
	 */
	private def buildConstraint(Closure closure) {
		def changelog = new DatabaseChangeLog()
		changelog.changeLogParameters = new ChangeLogParameters()

		def delegate = new ConstraintDelegate(databaseChangeLog: changelog,
						changeSetId: 'test-change-set',
						changeName: 'test-change')
		closure.delegate = delegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()

		delegate.constraint
	}

}

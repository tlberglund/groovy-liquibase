/*
 * Copyright 2011-2015 Tim Berglund and Steven C. Saliman
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

package org.liquibase.groovy.delegate

import liquibase.sql.visitor.PrependSqlVisitor
import org.junit.Test
import static org.junit.Assert.*
import liquibase.change.core.RawSQLChange
import liquibase.change.core.SQLFileChange
import liquibase.change.core.ExecuteShellCommandChange
import liquibase.change.custom.CustomChangeWrapper
import liquibase.resource.FileSystemResourceAccessor

/**
 * This is one of several classes that test the creation of refactoring changes
 * for ChangeSets. This particular class tests custom changes such as
 * {@code sql} and {@code executeCommand}
 * <p>
 * Since the Groovy DSL parser is meant to act as a pass-through for Liquibase
 * itself, it doesn't do much in the way of error checking.  For example, we
 * aren't concerned with whether or not required attributes are present - we
 * leave that to Liquibase itself.  In general, each change will have 3 kinds
 * of tests:<br>
 * <ol>
 * <li>A test with an empty parameter map, and if supported, an empty closure.
 * This kind of test will make sure that the Groovy parser doesn't introduce
 * any unintended attribute defaults for a change.</li>
 * <li>A test that sets all the attributes known to be supported by Liquibase
 * at this time.  This makes sure that the Groovy parser will send any given
 * groovy attribute to the correct place in Liquibase.  For changes that allow
 * a child closure, this test will include just enough in the closure to make
 * sure it gets processed, and that the right kind of closure is called.</li>
 * <li>Some tests take columns or a where clause in a child closure.  The same
 * closure handles both, but should reject one or the other based on how the
 * closure gets called. These changes will have an additional test with an
 * invalid closure to make sure it sets up the closure properly</li>
 * </ol>
 * <p>
 * Some changes require a little more testing, such as the {@code sql} change
 * that can receive sql as a string, or as a closure, or the {@code delete}
 * change, which is valid both with and without a child closure.
 * <p>
 * We don't worry about testing combinations that don't make sense, such as
 * allowing a createIndex change a closure, but no attributes, since it doesn't
 * make sense to have this kind of change without both a table name and at
 * least one column.  If a user tries it, they will get errors from Liquibase
 * itself.
 *
 * @author Steven C. Saliman
 */
class CustomRefactoringTests extends ChangeSetTests {

	/**
	 * Test parsing a sql change when we have an empty attribute map and an
	 * empty closure to make sure we don't get any unintended defaults. Also test
	 * our assumption that Liquibase will default splitStatements to true and
	 * stripComments to false.
	 */
	@Test
	void sqlWithoutAttributesOrClosure() {
		buildChangeSet {
			sql ([:]) {}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof RawSQLChange
		assertNull changes[0].dbms
		assertNull changes[0].endDelimiter
		assertTrue changes[0].splitStatements
		assertFalse changes[0].stripComments
		assertNull changes[0].sql
		assertNull changes[0].comment
		assertNoOutput()
	}

	/**
	 * Test parsing a sql change when we have no attributes or a closure, just
	 * a string.
	 */
	@Test
	void sqlIsString() {
		buildChangeSet {
			sql "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)"
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof RawSQLChange
		assertNull changes[0].dbms
		assertNull changes[0].endDelimiter
		assertTrue changes[0].splitStatements
		assertFalse changes[0].stripComments
		assertEquals "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)", changes[0].sql
		assertNull changes[0].comment
		assertNoOutput()
	}

	/**
	 * test parsing a sql change where we only have SQL in a closure.
	 */
	@Test
	void sqlInClosure() {
		buildChangeSet {
			sql {
				"UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)"
			}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertNull changes[0].dbms
		assertNull changes[0].endDelimiter
		assertTrue changes[0].splitStatements
		assertFalse changes[0].stripComments
		assertEquals "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)", changes[0].sql
		assertNull changes[0].comment
		assertNoOutput()
	}

	/**
	 * Test parsing a sql change when we have no attributes, but we do have a
	 * comment in the closure.
	 */
	@Test
	void sqlCommentInClosure() {
		buildChangeSet {
			sql {
				comment("No comment")
				"UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)"
			}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertNull changes[0].dbms
		assertNull changes[0].endDelimiter
		assertTrue changes[0].splitStatements
		assertFalse changes[0].stripComments
		assertEquals "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)", changes[0].sql
		assertEquals "No comment", changes[0].comment
		assertNoOutput()
	}

	/**
	 * Test parsing a sql chanve when we have all supported attributes present
	 * and no comments in the closure.  For this test set the two booleans to
	 * the opposite of the Liquibase defaults.
	 */
	@Test
	void sqlFullWithNoComments() {
		buildChangeSet {
			sql(dbms: 'oracle',
					splitStatements: false,
  				stripComments: true,
					endDelimiter: '!') {
				"UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)"
			}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof RawSQLChange
		assertEquals 'oracle', changes[0].dbms
		assertFalse changes[0].splitStatements
		assertTrue changes[0].stripComments
		assertEquals '!', changes[0].endDelimiter
		assertEquals "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)", changes[0].sql
		assertNull changes[0].comment
		assertNoOutput()
	}

	/**
	 * Test parsing a sql change when we have all attributes and we have a comment
	 * in the closure.  For this test we only set splitStatements to true.
	 */
	@Test
	void sqlFullWithComments() {
		buildChangeSet {
			sql(dbms: 'oracle',
							splitStatements: false,
							stripComments: true,
							endDelimiter: '!') {
				comment("No comment")
				"UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)"
			}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof RawSQLChange
		assertEquals 'oracle', changes[0].dbms
		assertFalse changes[0].splitStatements
		assertTrue changes[0].stripComments
		assertEquals '!', changes[0].endDelimiter
		assertEquals "UPDATE monkey SET emotion='ANGRY' WHERE id IN (1,2,3,4,5)", changes[0].sql
		assertEquals "No comment", changes[0].comment
		assertNoOutput()
	}

	/**
	 * Test parsing a sqlFile change with minimal attributes to confirm our
	 * assumptions about Liquibase defaults, which we assume to be true for
	 * splitStatements and false for stripComments.  We can't test this one with
	 * totally empty attributes because a sqlFile change will attempt to open the
	 * file immediately to work around a Liquibase bug.  This also means the file
	 * in question must exist.
	 */
	@Test
	void sqlFileEmpty() {
		resourceAccessor = new FileSystemResourceAccessor()
		buildChangeSet {
			sqlFile(path: 'src/test/changelog/file.sql')
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof SQLFileChange
		assertEquals 'src/test/changelog/file.sql', changes[0].path
		assertNull changes[0].relativeToChangelogFile
		assertNull changes[0].encoding
		assertFalse changes[0].isStripComments()
		assertTrue changes[0].isSplitStatements()
		assertNull changes[0].endDelimiter
		assertNull changes[0].dbms
		assertNotNull 'SQLFileChange.resourceAccessor cannot be null', changes[0].resourceAccessor
		assertNoOutput()
	}

	/**
	 * Test parsing a sqlFile change when we have all supported options. For this
	 * test, we set the two booleans to be the opposite of their default values.
	 */
	@Test
	void sqlFileFull() {
		resourceAccessor = new FileSystemResourceAccessor()
		buildChangeSet {
			sqlFile(path: 'src/test/changelog/file.sql',
							relativeToChangelogFile: false,
							stripComments: true,
							splitStatements: false,
							encoding: 'UTF-8',
							endDelimiter: '@',
			        dbms: 'oracle')
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof SQLFileChange
		assertEquals 'src/test/changelog/file.sql', changes[0].path
		assertFalse changes[0].relativeToChangelogFile
		assertEquals 'UTF-8', changes[0].encoding
		assertTrue changes[0].isStripComments()
		assertFalse changes[0].isSplitStatements()
		assertEquals '@', changes[0].endDelimiter
		assertEquals 'oracle', changes[0].dbms
		assertNotNull 'SQLFileChange.resourceAccessor cannot be null', changes[0].resourceAccessor
		assertNoOutput()
	}

	/**
	 * Test parsing an executeCommand with no args and an empty closure to make
	 * sure the DSL doesn't introduce any unintended defaults.
	 */
	@Test
	void executeCommandEmptyMapEmptyClosure() {
		buildChangeSet {
			executeCommand([:]) {	}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof ExecuteShellCommandChange
		assertNull changes[0].executable
		assertNull changes[0].os
		def args = changes[0].args
		assertNotNull args
		assertEquals 0, args.size()
		assertNoOutput()
	}

	/**
	 * Test parsing an executeCommand change when we have no attributes and there
	 * is no closure.
	 */
	@Test
	void executeCommandEmptyMapNoClosure() {
		buildChangeSet {
			executeCommand([:])
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof ExecuteShellCommandChange
		assertNull changes[0].executable
		assertNull changes[0].os
		def args = changes[0].args
		assertNotNull args
		assertEquals 0, args.size()
		assertNoOutput()
	}

	/**
	 * Test parsing executeCommand when we have all supported attributes,but
	 * no argument closure.
	 */
	@Test
	void executeCommandWithNoArgs() {
		buildChangeSet {
			executeCommand(executable: "awk '/monkey/ { count++ } END { print count }'",
							       os: 'Mac OS X, Linux')
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof ExecuteShellCommandChange
		assertEquals "awk '/monkey/ { count++ } END { print count }'", changes[0].executable
		assertNotNull changes[0].os
		assertEquals 2, changes[0].os.size
		assertEquals 'Mac OS X', changes[0].os[0]
		assertEquals 'Linux', changes[0].os[1]
		def args = changes[0].args
		assertNotNull args
		assertEquals 0, args.size()
		assertNoOutput()
	}

	/**
	 * Test parsing an executeCommand change where the arguments are maps, like
	 * the XML would do it.
	 */
	@Test
	void executeCommandWithArgsInMap() {
		buildChangeSet {
			executeCommand(executable: "awk", os: 'Mac OS X, Linux') {
				arg(value: '/monkey/ { count++ } END { print count }')
				arg(value: '-f database.log')
			}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof ExecuteShellCommandChange
		assertEquals "awk", changes[0].executable
		assertNotNull changes[0].os
		assertEquals 2, changes[0].os.size
		assertEquals 'Mac OS X', changes[0].os[0]
		assertEquals 'Linux', changes[0].os[1]
		def args = changes[0].args
		assertNotNull args
		assertEquals 2, args.size()
		assertTrue args.every { arg -> arg instanceof String }
		assertEquals '/monkey/ { count++ } END { print count }', args[0]
		assertEquals '-f database.log', args[1]
		assertNoOutput()
	}

	/**
	 * Test parsing an executeCommand change where the arguments are just Strings.
	 * This is not the way the XML does it, but it is the way the Groovy DSL has
	 * always done it, and it is nice shorthand.
	 */
	@Test
	void executeCommandWithStringArgs() {
		buildChangeSet {
			executeCommand(executable: "awk", os: 'Mac OS X, Linux') {
				arg('/monkey/ { count++ } END { print count }')
				arg('-f database.log')
			}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof ExecuteShellCommandChange
		assertEquals "awk", changes[0].executable
		assertNotNull changes[0].os
		assertEquals 2, changes[0].os.size
		assertEquals 'Mac OS X', changes[0].os[0]
		assertEquals 'Linux', changes[0].os[1]
		def args = changes[0].args
		assertNotNull args
		assertEquals 2, args.size()
		assertTrue args.every { arg -> arg instanceof String }
		assertEquals '/monkey/ { count++ } END { print count }', args[0]
		assertEquals '-f database.log', args[1]
		assertNoOutput()
	}


	@Test
	void customRefactoringWithClassAndNoParameters() {
		buildChangeSet {
			customChange(class: 'org.liquibase.change.custom.MonkeyChange')
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CustomChangeWrapper
		assertEquals 'org.liquibase.change.custom.MonkeyChange', changes[0].className
		assertNoOutput()
	}


	@Test
	void customRefactoringWithClassAndParameters() {
		buildChangeSet {
			customChange(class: 'org.liquibase.change.custom.MonkeyChange') {
				emotion('angry')
				'rfid-tag'(28763)
			}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CustomChangeWrapper
		assertEquals 'org.liquibase.change.custom.MonkeyChange', changes[0].className
		def args = changes[0].paramValues
		assertNotNull args
		assertEquals 2, args.size()
		assertTrue args.containsKey('emotion')
		assertTrue args.containsKey('rfid-tag')
		assertEquals 'angry', args.emotion
		assertEquals '28763', args.'rfid-tag'
		assertNoOutput()
	}

	/**
	 * Make sure modifySql works.  Most of the tests for this are in
	 * {@link ModifySqlDelegateTests}, this just needs to make sure that the
	 * SqlVisitors that the delegate returns are added to the changeSet.  This
	 * one also tests that we can have a modifySql with no attributes of its own.
	 */
	@Test
	void modifySqlValid() {
		buildChangeSet {
			modifySql {
				prepend(value: 'engine INNODB')
			}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 0, changes.size()
		assertEquals 1, changeSet.sqlVisitors.size()
		assertTrue changeSet.sqlVisitors[0] instanceof PrependSqlVisitor
		assertEquals 'engine INNODB', changeSet.sqlVisitors[0].value
		assertNull changeSet.sqlVisitors[0].applicableDbms
		assertNull changeSet.sqlVisitors[0].contexts
		assertFalse changeSet.sqlVisitors[0].applyToRollback
		assertNoOutput()


	}
}


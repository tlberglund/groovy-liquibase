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

import liquibase.change.CheckSum
import liquibase.change.core.AddColumnChange
import liquibase.change.core.DeleteDataChange
import liquibase.change.core.DropColumnChange
import liquibase.change.core.RawSQLChange
import liquibase.change.core.UpdateDataChange
import liquibase.exception.RollbackImpossibleException
import org.junit.Test
import org.junit.Ignore
import static org.junit.Assert.*


class ChangeSetMethodTests extends ChangeSetTests {
	@Test
	void testComments() {
		buildChangeSet {
			comment "This is a comment"
		}
		assertEquals "This is a comment", changeSet.comments
		assertNoOutput()
	}


	@Ignore
	@Test
	void testValidChecksum() {
		def checksum = 'd0763edaa9d9bd2a9516280e9044d885'
		def liquibaseChecksum = CheckSum.parse(checksum)
		assertFalse "Arbitrary checksum should not be valid before being added", changeSet.isCheckSumValid(liquibaseChecksum)
		buildChangeSet {
			validCheckSum checksum
		}
		assertTrue "Arbitrary checksum should be valid after being added", changeSet.isCheckSumValid(liquibaseChecksum)
		assertNoOutput()
	}

	/**
	 * Test a rollback with a single statement passed as a string.
	 */
	@Test
	void rollbackString() {
		def rollbackSql = 'DROP TABLE monkey'
		buildChangeSet {
			rollback rollbackSql
		}

		assertEquals 0, changeSet.changes.size()
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 1, changes.size()
		assertEquals(new RawSQLChange("DROP TABLE monkey").sql, changes[0].sql)
		assertNoOutput()
	}


	/**
	 * Test rollback with two statements passed as strings.
	 */
	@Test
	void rollbackTwoStrings() {
		def rollbackSql = """UPDATE monkey_table SET emotion='angry' WHERE status='PENDING';
ALTER TABLE monkey_table DROP COLUMN angry;"""
		buildChangeSet {
			rollback rollbackSql
		}
		assertEquals 0, changeSet.changes.size()
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 2, changes.size()
		assertEquals(new RawSQLChange("UPDATE monkey_table SET emotion='angry' WHERE status='PENDING'").sql, changes[0].sql)
		assertEquals(new RawSQLChange("ALTER TABLE monkey_table DROP COLUMN angry").sql, changes[1].sql)
		assertNoOutput()
	}

	/**
	 * Rollback one statement given in a closure
	 */
	@Test
	void rollbackOneStatementInClosure() {
		buildChangeSet {
			rollback {
				"""UPDATE monkey_table SET emotion='angry' WHERE status='PENDING'"""
			}
		}

		assertEquals 0, changeSet.changes.size()
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 1, changes.size()
		assertEquals(new RawSQLChange("UPDATE monkey_table SET emotion='angry' WHERE status='PENDING'").sql, changes[0].sql)
		assertNoOutput()
	}

	/**
	 * Rollback two statements given in a closure
	 */
	@Test
	void rollbackTwoStatementInClosure() {
		buildChangeSet {
			rollback {
				"""UPDATE monkey_table SET emotion='angry' WHERE status='PENDING';
ALTER TABLE monkey_table DROP COLUMN angry;"""
			}
		}

		assertEquals 0, changeSet.changes.size()
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 2, changes.size()
		assertEquals(new RawSQLChange("UPDATE monkey_table SET emotion='angry' WHERE status='PENDING'").sql, changes[0].sql)
		assertEquals(new RawSQLChange("ALTER TABLE monkey_table DROP COLUMN angry").sql, changes[1].sql)
		assertNoOutput()
	}

	/**
	 * Rollback one change when the change is a nested refactoring.
	 */
	@Test
	void rollbackOneNestedChange() {
		buildChangeSet {
			rollback {
				delete(tableName: 'monkey')
			}
		}

		assertEquals 0, changeSet.changes.size()
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DeleteDataChange
		assertEquals 'monkey', changes[0].tableName
		assertNoOutput()
	}

	/**
	 * Rollback with two changes when they are both given as nested refactorings.
	 * We don't care too much about the content of the resultant changes, just
	 * that the right changes of the right type wind up in the right place.
	 */
	@Test
	void rollbackTwoNestedChange() {
		buildChangeSet {
			rollback {
				update(tableName: 'monkey') {
					column(name: 'emotion', value: 'angry')
				}
				dropColumn(tableName: 'monkey', columnName: 'emotion')
			}
		}

		assertEquals 0, changeSet.changes.size()
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 2, changes.size()
		assertTrue changes[0] instanceof UpdateDataChange
		assertEquals 'monkey', changes[0].tableName
		assertTrue changes[1] instanceof DropColumnChange
		assertEquals 'monkey', changes[1].tableName
		assertEquals 'emotion', changes[1].columnName
		assertNoOutput()
	}

	/**
	 * This is a wacky combination. Let's use a refactoring paired with raw SQL.
	 * I don't know wha the XML parser does, but the Groovy parser
	 */
	@Test
	void rollbackCombineRefactoringWithSql() {
		buildChangeSet {
			rollback {
				update(tableName: 'monkey') {
					column(name: 'emotion', value: 'angry')
				}
				"ALTER TABLE monkey_table DROP COLUMN angry;"
			}
		}

		assertEquals 0, changeSet.changes.size()
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 2, changes.size()
		assertTrue changes[0] instanceof UpdateDataChange
		assertEquals 'monkey', changes[0].tableName
		assertEquals(new RawSQLChange("ALTER TABLE monkey_table DROP COLUMN angry").sql, changes[1].sql)
		assertNoOutput()
	}

	/**
	 * Process a map based rollback that is missing the changeSetId.  Expect an
	 * error.
	 */
	@Test(expected = RollbackImpossibleException)
	void rollbackMissingId() {
		buildChangeSet {
			rollback(changeSetAuthor: 'darwin')
		}
	}

	/**
	 * Process a map based rollback when the referenced change cannot be found.
	 */
	@Test(expected = RollbackImpossibleException)
	void rollbackInvalidChange() {
		buildChangeSet {
			rollback(changeSetId: 'big-bang', changeSetAuthor: CHANGESET_AUTHOR)
		}
	}

	// rollback map without path /filePath
	/**
	 * Process a map based rollback when we don't supply a file path.  In that
	 * case, we should use the one in the databaseChangeLog.  This test needs to
	 * use the same constants in the rollback as was used to create the changeSet
	 * in the set up.
	 */
	@Test
	void rollbackWithoutPath() {
		buildChangeSet {
			addColumn(tableName: 'monkey') {
				column(name: 'diet', type: 'varchar(30)')
			}
			rollback(changeSetId: CHANGESET_ID, changeSetAuthor: CHANGESET_AUTHOR)
		}

		// in this case, we expect the addColumn change to also be the change
		// inside the rollback.
		assertEquals 1, changeSet.changes.size()
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddColumnChange
		assertEquals 'monkey', changes[0].tableName
		assertNoOutput()
	}

	/**
	 * Test a map based rollback that includes a path.  We can't really test this
	 * easily because we need a valid change with that path, but we can at least
	 * make sure the attribute is supported.
	 */
	@Test
	void rollbackWitPath() {
		buildChangeSet {
			addColumn(tableName: 'monkey') {
				column(name: 'diet', type: 'varchar(30)')
			}
			rollback(changeSetId: CHANGESET_ID, changeSetAuthor: CHANGESET_AUTHOR, changeSetPath: CHANGESET_FILEPATH)
		}

		// in this case, we expect the addColumn change to also be the change
		// inside the rollback.
		assertEquals 1, changeSet.changes.size()
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddColumnChange
		assertEquals 'monkey', changes[0].tableName
		assertNoOutput()

	}

	/**
	 * Test a map based rollback with the deprecated "id" and "author" attributes
	 * to make sure we get the deprecation warnings.
	 */
	@Test
	void rollbackWithDeprecatedAttributes() {
		buildChangeSet {
			addColumn(tableName: 'monkey') {
				column(name: 'diet', type: 'varchar(30)')
			}
			rollback(id: CHANGESET_ID, author: CHANGESET_AUTHOR)
		}

		// in this case, we expect the addColumn change to also be the change
		// inside the rollback.
		assertEquals 1, changeSet.changes.size()
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddColumnChange
		assertEquals 'monkey', changes[0].tableName
		assertPrinted "the id attribute of a rollback has been deprecated"
		assertPrinted "the author attribute of a rollback has been deprecated"

	}

	/**
	 * Test a change with an invalid attribute.  Since all changes funnel through
	 * the same method to process the attributes, we only need to test this once.
	 * For this test, we've chosen the dropTable change, but we've incorrectly
	 * set the cascadeToConstraints attribute instead of cascadeConstraints.  This
	 * should result in an exception being thrown.
	 */
	@Test(expected = IllegalArgumentException)
	void processChangeWithInvalidAttribute() {
		buildChangeSet {
			dropTable(catalogName: 'catalog',
							schemaName: 'schema',
							tableName: 'fail_table',
							cascadeToConstraints: true)
		}
	}

	// invalid method, such as createLink
}
/*
 * Copyright 2011-2014 Tim Berglund and Steven C. Saliman
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

package net.saliman.liquibase.delegate

import liquibase.change.CheckSum
import liquibase.change.core.AddColumnChange
import liquibase.change.core.DeleteDataChange
import liquibase.change.core.DropColumnChange
import liquibase.change.core.RawSQLChange
import liquibase.change.core.UpdateDataChange
import liquibase.exception.ChangeLogParseException
import liquibase.exception.RollbackImpossibleException
import org.junit.Test
import org.junit.Ignore
import static org.junit.Assert.*

/**
 * This class tests the methods of a change set that are neither preconditions,
 * nor refactorings.  This includes things like {@code rollback}
 *
 * @author Tim Berglund
 * @author Steven C. Saliman
 */
class ChangeSetMethodTests extends ChangeSetTests {
	@Test
	void comments() {
		buildChangeSet {
			comment "This is a comment"
		}
		assertEquals "This is a comment", changeSet.comments
		assertNoOutput()
	}

	/**
	 * Test the validChecksuum functionality.  This test needs some explanation.
	 * Liquibase's {@code isChecksumValid()} method compares the change set's
	 * current checksum to the hash given to the method. If they don't match, it
	 * will check the current checksum against checksums that are stored with the
	 * validChecksum element.  So to test this, we do the following:<br>
	 * <ol>
	 * <li>Call isChecksumValid with a bogus checksum.  Because the bogus checksum
	 * does not match the current checksum, we expect it to return false.</li>
	 * <li>apply the validCheckSum method with the current, valid, checksum for
	 * the changeSet.</li>
	 * <li>Call isChecksumValid with the same bogus checksum as before. It still
	 * won't match the current calculated checksum, but since that checksum has
	 * been marked as valid, we should now get a true result.
	 */
	@Test
	void validChecksumTest() {
		def checksum = 'd0763edaa9d9bd2a9516280e9044d885'
		def liquibaseChecksum = CheckSum.parse(checksum)
		def goodChecksum = changeSet.generateCheckSum().toString()
		assertFalse "Arbitrary checksum should not be valid before being added", changeSet.isCheckSumValid(liquibaseChecksum)
		buildChangeSet {
			validCheckSum goodChecksum
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
	 * Test a map based rollback with the deprecated "id" attribute to make sure
	 * we get a parse exception.
	 */
	@Test(expected = ChangeLogParseException)
	void rollbackWithDeprecatedId() {
		buildChangeSet {
			addColumn(tableName: 'monkey') {
				column(name: 'diet', type: 'varchar(30)')
			}
			rollback(id: CHANGESET_ID, changeSetAuthor: CHANGESET_AUTHOR)
		}

	}

	/**
	 * Test a map based rollback with the deprecated "id" attribute to make sure
	 * we get a parse exception.
	 */
	@Test(expected = ChangeLogParseException)
	void rollbackWithDeprecatedAuthor() {
		buildChangeSet {
			addColumn(tableName: 'monkey') {
				column(name: 'diet', type: 'varchar(30)')
			}
			rollback(changeSetId: CHANGESET_ID, author: CHANGESET_AUTHOR)
		}

	}
	/**
	 * Test a map based rollback with the deprecated "id" attribute to make sure
	 * we get a parse exception.
	 */
	@Test(expected = ChangeLogParseException)
	void rollbackWithInvalidAttribute() {
		buildChangeSet {
			addColumn(tableName: 'monkey') {
				column(name: 'diet', type: 'varchar(30)')
			}
			rollback(rollbackId: CHANGESET_ID, changeSetAuthor: CHANGESET_AUTHOR)
		}

	}

	/**
	 * Test a change with an invalid attribute.  Since all changes funnel through
	 * the same method to process the attributes, we only need to test this once.
	 * For this test, we've chosen the dropTable change, but we've incorrectly
	 * set the cascadeToConstraints attribute instead of cascadeConstraints.  This
	 * should result in an exception being thrown.
	 */
	@Test(expected = ChangeLogParseException)
	void processChangeWithInvalidAttribute() {
		buildChangeSet {
			dropTable(catalogName: 'catalog',
							schemaName: 'schema',
							tableName: 'fail_table',
							cascadeToConstraints: true)
		}
	}

	// invalid method, such as createLink
	@Test(expected = ChangeLogParseException)
	void processInvalidChange() {
		buildChangeSet {
			createLink(name: 'myLink')
		}
	}
}
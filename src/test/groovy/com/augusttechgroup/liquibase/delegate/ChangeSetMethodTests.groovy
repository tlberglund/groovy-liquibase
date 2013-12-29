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
import liquibase.change.core.RawSQLChange

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


	@Test
	void testRollbackString() {
		def rollbackSql = 'DROP TABLE monkey'
		buildChangeSet {
			rollback rollbackSql
		}
		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 1, changes.size()
		assertEquals(new RawSQLChange(rollbackSql).sql, changes[0].sql)
		assertNoOutput()
	}


	@Ignore
	@Test
	void testRollbackByChangeSetId() {
		buildChangeSet {
			rollback {
				"""UPDATE monkey_table SET emotion='angry' WHERE status='PENDING';
ALTER TABLE monkey_table DROP COLUMN angry;"""
			}
		}

		def changes = changeSet.getRollBackChanges()
		assertNotNull changes
		assertEquals 2, changes.size()
		assertEquals(new RawSQLChange("UPDATE monkey_table SET emotion='angry' WHERE status='PENDING'").sql, changes[0].sql)
		assertEquals(new RawSQLChange("ALTER TABLE monkey_table DROP COLUMN angry").sql, changes[1].sql)
		assertNoOutput()
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
}
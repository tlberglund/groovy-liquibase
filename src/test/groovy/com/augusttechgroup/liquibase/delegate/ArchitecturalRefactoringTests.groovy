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
import liquibase.change.ColumnConfig
import liquibase.change.core.CreateIndexChange
import liquibase.change.core.DropIndexChange

/**
 * This class tests ChangeSet refactoring changes that deal with indexes.
 * The tests make sure the DSL can parse each change correctly and handle all
 * options supported by Liquibase.  It does not worry about validating the
 * change itself (making sure required attributes are present for example),
 * that is done by Liquibase itself.
 */
class ArchitecturalRefactoringTests extends ChangeSetTests {

	/**
	 * Test parsing a createIndex changeSet with no attributes and an empty
	 * closure.  Because it makes no sense to have a createIndex without at
	 * least a table name and one column (Liquibase won't let that continue),
	 * we won't try to deal with missing closures or maps.  We should still get
	 * a collection of columns, it should just be empty.
	 */
	@Test
	void createIndexEmpty() {
		buildChangeSet {
			createIndex([:]) { }
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateIndexChange
		assertNull changes[0].tableName
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tablespace
		assertNull changes[0].indexName
		assertNull changes[0].unique
		def columns = changes[0].columns
		assertNotNull columns
		assertEquals 0, columns.size()
	}

	/**
	 * Test parsing a createIndex change with all attributes set and one column.
	 * We don't really care too much about the particulars of the column, since
	 * column parsing is tested in the ColumnDelegate tests.
	 */
	@Test
	void createIndexFullOneColumn() {
		buildChangeSet {
			createIndex(catalogName: 'catalog',
							    schemaName: 'schema',
							    tableName: 'monkey',
							    tablespace: 'tablespace',
							    indexName: 'ndx_monkeys',
							    unique: true) {
				column(name: 'name')
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateIndexChange
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'tablespace', changes[0].tablespace
		assertEquals 'ndx_monkeys', changes[0].indexName
		assertTrue changes[0].unique
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof ColumnConfig }
		assertEquals 1, columns.size()
		assertEquals 'name', columns[0].name
	}

	/**
	 * Test parsing a createIndex change with more than one column to make sure
	 * we get them both.
	 */
	@Test
	void createIndexMultipleColumns() {
		buildChangeSet {
			createIndex(catalogName: 'catalog',
							    schemaName: 'schema',
							    tableName: 'monkey',
							    tablespace: 'tablespace',
							    indexName: 'ndx_monkeys',
							    unique: true) {
				column(name: 'species')
				column(name: 'name')
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateIndexChange
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'tablespace', changes[0].tablespace
		assertEquals 'ndx_monkeys', changes[0].indexName
		assertTrue changes[0].unique
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof ColumnConfig }
		assertEquals 2, columns.size()
		assertEquals 'species', columns[0].name
		assertEquals 'name', columns[1].name
	}

	/**
	 * Test parsing a dropIndex change with no attributes to make sure the DSL
	 * doesn't introduce unexpected defaults.
	 */
	@Test
	void dropIndexEmpty() {
		buildChangeSet {
			dropIndex([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropIndexChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].indexName
	}

	/**
	 * Test parsing a dropIndex change with all supported attributes.
	 */
	@Test
	void dropIndexFull() {
		buildChangeSet {
			dropIndex(catalogName: 'catalog',
							  schemaName: 'schema',
							  tableName: 'monkey',
							  indexName: 'ndx_monkeys')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropIndexChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'ndx_monkeys', changes[0].indexName
	}
}


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
 * This is one of several classes that test the creation of refactoring changes
 * for ChangeSets. This particular class tests changes that deal with indexes.
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
 * @author Tim Berglund
 * @author Steven C. Saliman
 */
class ArchitecturalRefactoringTests extends ChangeSetTests {

	/**
	 * Test parsing a createIndex changeSet with no attributes and an empty
	 * closure.
	 */
	@Test
	void createIndexEmpty() {
		buildChangeSet {
			createIndex([:]) { }
		}

		assertEquals 0, changeSet.getRollBackChanges().length
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
		assertNull changes[0].associatedWith
		def columns = changes[0].columns
		assertNotNull columns
		assertEquals 0, columns.size()
		assertNoOutput()
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
							    unique: true,
			            associatedWith: 'foreignKey') {
				column(name: 'name')
			}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateIndexChange
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'tablespace', changes[0].tablespace
		assertEquals 'ndx_monkeys', changes[0].indexName
		assertEquals 'foreignKey', changes[0].associatedWith
		assertTrue changes[0].unique
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof ColumnConfig }
		assertEquals 1, columns.size()
		assertEquals 'name', columns[0].name
		assertNoOutput()
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
							    unique: true,
							    associatedWith: 'foreignKey') {
				column(name: 'species')
				column(name: 'name')
			}
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateIndexChange
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'tablespace', changes[0].tablespace
		assertEquals 'ndx_monkeys', changes[0].indexName
		assertEquals 'foreignKey', changes[0].associatedWith
		assertTrue changes[0].unique
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof ColumnConfig }
		assertEquals 2, columns.size()
		assertEquals 'species', columns[0].name
		assertEquals 'name', columns[1].name
		assertNoOutput()
	}

	/**
	 * The createIndex change can take columns, but a where clause is not valid.
	 * Test parsing a createIndex change with a where clause to make sure it gets
	 * rejected.
	 */
	@Test(expected = IllegalArgumentException)
	void createIndexWithWhereClause() {
		buildChangeSet {
			createIndex(catalogName: 'catalog',
							schemaName: 'schema',
							tableName: 'monkey',
							tablespace: 'tablespace',
							indexName: 'ndx_monkeys',
							unique: true,
							associatedWith: 'foreignKey') {
				where "it doesn't matter"
			}
		}
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

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropIndexChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].indexName
		assertNoOutput()
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
							  indexName: 'ndx_monkeys',
			          associatedWith: 'foreignKey')
		}

		assertEquals 0, changeSet.getRollBackChanges().length
		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropIndexChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'ndx_monkeys', changes[0].indexName
		assertEquals 'foreignKey', changes[0].associatedWith
		assertNoOutput()
	}
}


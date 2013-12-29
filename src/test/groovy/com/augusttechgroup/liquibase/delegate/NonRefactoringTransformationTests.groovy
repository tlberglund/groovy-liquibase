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

import liquibase.change.core.InsertDataChange
import liquibase.change.ColumnConfig
import liquibase.change.core.LoadDataChange
import liquibase.change.core.LoadDataColumnConfig
import liquibase.change.core.LoadUpdateDataChange
import liquibase.change.core.UpdateDataChange
import liquibase.change.core.TagDatabaseChange
import liquibase.change.core.StopChange
import liquibase.resource.FileSystemResourceAccessor
import liquibase.change.core.DeleteDataChange

/**
 * This class tests ChangeSet refactoring changes that deal with data, such as
 * inserts and deletes.  The tests make sure the DSL can parse each change
 * correctly and handle all options supported by Liquibase.  It does not worry
 * about validating the change itself (making sure required attributes are
 * present for example), that is done by Liquibase itself.
 */
class NonRefactoringTransformationTests extends ChangeSetTests {

	/**
	 * Test parsing an insert change with no attributes and no columns to make
	 * sure the DSL doesn't introduce any unexpected defaults.
	 */
	@Test
	void insertEmpty() {
		buildChangeSet {
			insert([:]) {	}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof InsertDataChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].dbms
		assertNoOutput()
	}

	/**
	 * Test parsing an insert when we have all supported attributes and some
	 * columns.  We don't need to worry about columns without attributes or
	 * attributes without columns because those scenarios don't make any sense.
	 */
	@Test
	void insertFull() {
		def now = '2010-11-02 07:52:04'
		def sqlNow = parseSqlTimestamp(now)
		buildChangeSet {
			insert(catalogName: 'catalog',
						 schemaName: 'schema',
						 tableName: 'monkey',
			       dbms: 'oracle, db2') {
				column(name: 'id', valueNumeric: 502)
				column(name: 'emotion', value: 'angry')
				column(name: 'last_updated', valueDate: now)
				column(name: 'active', valueBoolean: true)
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof InsertDataChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'oracle, db2', changes[0].dbms
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof ColumnConfig }
		assertEquals 4, columns.size()
		assertEquals 'id', columns[0].name
		assertEquals 502, columns[0].valueNumeric
		assertEquals 'emotion', columns[1].name
		assertEquals 'angry', columns[1].value
		assertEquals 'last_updated', columns[2].name
		assertEquals sqlNow, columns[2].valueDate
		assertEquals 'active', columns[3].name
		assertTrue columns[3].valueBoolean
		assertNoOutput()
	}

	/**
	 * Test parsing a loadData change when the attribute map and column closure
	 * are both empty.  We don't need to worry about the map or the closure
	 * being missing because that kind of change doesn't make sense.  In this
	 * case, Liquibase itself has defaults for the separator and quote chars,
	 * which is what we check in the test.
	 */
	@Test
	void loadDataEmpty() {
		resourceAccessor = new FileSystemResourceAccessor()
		buildChangeSet {
			loadData([:]) {	}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof LoadDataChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].file
		assertNull changes[0].encoding
		assertEquals ",", changes[0].separator
		assertEquals '"', changes[0].quotchar
		assertNotNull 'LoadDataChange.resourceAccessor should not be null', changes[0].resourceAccessor
		def columns = changes[0].columns
		assertNotNull columns
		assertEquals 0, columns.size()
		assertNoOutput()
	}

	/**
	 * Test parsing a loadDataChange with all supported attributes and a few
	 * columns.  We're not too concerned with the column contents, just make sure
	 * we get them.  For this test, we want a separator and quotchar that is
	 * different from the Liquibase defaults, so we'll go with  semi-colon
	 * separated and single quoted
	 */
	@Test
	void loadDataFull() {
		resourceAccessor = new FileSystemResourceAccessor()

		buildChangeSet {
			loadData(catalogName: 'catalog',
							 schemaName: 'schema',
							 tableName: 'monkey',
							 file: 'data.csv',
							 encoding: 'UTF-8',
							 separator: ';',
							 quotchar: "'") {
				column(name: 'id')
				column(name: 'emotion')
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof LoadDataChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'data.csv', changes[0].file
		assertEquals 'UTF-8', changes[0].encoding
		assertEquals ';', changes[0].separator
		assertEquals "'", changes[0].quotchar
		assertNotNull 'LoadDataChange.resourceAccessor should not be null', changes[0].resourceAccessor
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof LoadDataColumnConfig }
		assertEquals 2, columns.size()
		assertEquals 'id', columns[0].name
		assertEquals 'emotion', columns[1].name
		assertNoOutput()
	}

	/**
	 * Test parsing a loadData change when the file name is actually a File
	 * object.  Again, we're not validating the columns, just that they are
	 * present.  Using a File object has been deprecated, so look for a warning
	 * in standard out.
	 */
	@Test
	void loadDataFullWithFile() {
		buildChangeSet {
			loadData(catalogName: 'catalog',
							 schemaName: 'schema',
							 tableName: 'monkey',
							 file: new File('data.csv'),
							 encoding: 'UTF-8',
							 separator: ';',
							 quotchar: '"') {
				column(name: 'id')
				column(name: 'emotion')
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof LoadDataChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals new File('data.csv').canonicalPath, changes[0].file
		assertEquals 'UTF-8', changes[0].encoding
		assertEquals ';', changes[0].separator
		assertEquals '"', changes[0].quotchar
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof LoadDataColumnConfig }
		assertEquals 2, columns.size()
		assertEquals 'id', columns[0].name
		assertEquals 'emotion', columns[1].name
		assertPrinted "using a File object for loadData's 'file' attribute has been deprecated"
	}

	/**
	 * Test parsing a loadData change when the attribute map and column closure
	 * are both empty.  We don't need to worry about the map or the closure
	 * being missing because that kind of change doesn't make sense.  In this
	 * case, Liquibase itself has defaults for the separator and quote chars,
	 * which is what we check in the test.
	 */
	@Test
	void loadUpdateDataEmpty() {
		resourceAccessor = new FileSystemResourceAccessor()
		buildChangeSet {
			loadUpdateData([:]) {	}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof LoadUpdateDataChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].file
		assertNull changes[0].encoding
		assertEquals ",", changes[0].separator
		assertEquals '"', changes[0].quotchar
		assertNotNull 'LoadDataChange.resourceAccessor should not be null', changes[0].resourceAccessor
		def columns = changes[0].columns
		assertNotNull columns
		assertEquals 0, columns.size()
		assertNoOutput()
	}

	/**
	 * Test parsing a loadDataChange with all supported attributes and a few
	 * columns.  We're not too concerned with the column contents, just make sure
	 * we get them.  For this test, we want a separator and quotchar that is
	 * different from the Liquibase defaults, so we'll go with  semi-colon
	 * separated and single quoted
	 */
	@Test
	void loadUpdateDataFull() {
		resourceAccessor = new FileSystemResourceAccessor()

		buildChangeSet {
			loadUpdateData(catalogName: 'catalog',
							schemaName: 'schema',
							tableName: 'monkey',
							file: 'data.csv',
							encoding: 'UTF-8',
							separator: ';',
							quotchar: "'") {
				column(name: 'id')
				column(name: 'emotion')
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof LoadUpdateDataChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'data.csv', changes[0].file
		assertEquals 'UTF-8', changes[0].encoding
		assertEquals ';', changes[0].separator
		assertEquals "'", changes[0].quotchar
		assertNotNull 'LoadDataChange.resourceAccessor should not be null', changes[0].resourceAccessor
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof LoadDataColumnConfig }
		assertEquals 2, columns.size()
		assertEquals 'id', columns[0].name
		assertEquals 'emotion', columns[1].name
		assertNoOutput()
	}

	/**
	 * Test parsing a loadData change when the file name is actually a File
	 * object.  Again, we're not validating the columns, just that they are
	 * present. Using a File object has been deprecated, so look for a warning
	 * in standard out.
	 */
	@Test
	void loadUpdateDataFullWithFile() {
		buildChangeSet {
			loadUpdateData(catalogName: 'catalog',
							schemaName: 'schema',
							tableName: 'monkey',
							file: new File('data.csv'),
							encoding: 'UTF-8',
							separator: ';',
							quotchar: '"') {
				column(name: 'id')
				column(name: 'emotion')
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof LoadUpdateDataChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals new File('data.csv').canonicalPath, changes[0].file
		assertEquals 'UTF-8', changes[0].encoding
		assertEquals ';', changes[0].separator
		assertEquals '"', changes[0].quotchar
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof LoadDataColumnConfig }
		assertEquals 2, columns.size()
		assertEquals 'id', columns[0].name
		assertEquals 'emotion', columns[1].name
		assertPrinted "using a File object for loadUpdateData's 'file' attribute has been deprecated"
	}

	/**
	 * test parsing an updateData change with no attributes and no closure to
	 * make sure the DSL is not adding any unintended defaults.
	 */
  // empty
	@Test
	void updateDataEmpty() {
		buildChangeSet {
			update([:]) {	}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof UpdateDataChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].whereClause
		def columns = changes[0].columns
		assertNotNull columns
		assertEquals 0, columns.size()
		assertNoOutput()
	}

	/**
	 * Test parsing an updateData change when we have all supported attributes,
	 * and a couple of columns, but no where clause.  This should not cause an
	 * issue. As always, we don't care about the contents of the columns.
	 */
	@Test
	void updateDataNoWhere() {
		buildChangeSet {
			update(catalogName: 'catalog',  schemaName: 'schema', tableName: 'monkey') {
				column(name: 'rfid_tag')
				column(name: 'emotion')
				column(name: 'last_updated')
				column(name: 'active')
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof UpdateDataChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertNull changes[0].whereClause
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof ColumnConfig }
		assertEquals 4, columns.size()
		assertEquals 'rfid_tag', columns[0].name
		assertEquals 'emotion', columns[1].name
		assertEquals 'last_updated', columns[2].name
		assertEquals 'active', columns[3].name
		assertNoOutput()
	}

	/**
	 * Test parsing an updateData change when we have attributes, columns and
	 * a where clause.
	 */
	@Test
	void updateDataFull() {
		buildChangeSet {
			update(catalogName: 'catalog',  schemaName: 'schema', tableName: 'monkey') {
				column(name: 'rfid_tag')
				column(name: 'emotion')
				column(name: 'last_updated')
				column(name: 'active')
				where "id=882"
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof UpdateDataChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'id=882', changes[0].whereClause
		def columns = changes[0].columns
		assertNotNull columns
		assertTrue columns.every { column -> column instanceof ColumnConfig }
		assertEquals 4, columns.size()
		assertEquals 'rfid_tag', columns[0].name
		assertEquals 'emotion', columns[1].name
		assertEquals 'last_updated', columns[2].name
		assertEquals 'active', columns[3].name
		assertNoOutput()
	}

	/**
	 * Test parsing a delete change with no attributes and no where clause.  This
	 * just makes sure the DSL doesn't introduce any unexpected defaults.
	 */
	@Test
	void deleteDataEmpty() {
		buildChangeSet {
			delete([:]) { }
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DeleteDataChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].whereClause
		assertNoOutput()
	}

	/**
	 * Test parsing a delete change when we have all attributes and a where clause
	 */
	@Test
	void deleteDataFull() {
		buildChangeSet {
			delete(catalogName: 'catalog', schemaName: 'schema', tableName: 'monkey') {
				where "emotion='angry' AND active=true"
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DeleteDataChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals "emotion='angry' AND active=true", changes[0].whereClause
		assertNoOutput()
	}

	/**
	 * Test parsing a delete change without a closure. This just means we have
	 * no "where" clause, and should be supported.
	 */
	@Test
	void deleteDataNoWhereClause() {
		buildChangeSet {
			delete(catalogName: 'catalog', schemaName: 'schema', tableName: 'monkey')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DeleteDataChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertNull changes[0].whereClause
		assertNoOutput()
	}

	/**
	 * Test parsing a tagDatabase change when we have no attributes to make sure
	 * the DSL doesn't introduce any defaults.
	 */
	@Test
	void tagDatabaseEmpty() {
		buildChangeSet {
			tagDatabase([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof TagDatabaseChange
		assertNull changes[0].tag
		assertNoOutput()
	}

	/**
	 * Test parsing a tagDatabase change when we have all supported attributes.
	 */
	@Test
	void tagDatabaseNameInAttributes() {
		buildChangeSet {
			tagDatabase(tag: 'monkey')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof TagDatabaseChange
		assertEquals 'monkey', changes[0].tag
		assertNoOutput()
	}

	/**
	 * Test parsing a tagDatabase change when the name is not in an attribute.
	 */
	@Test
	void tagDatabaseNameIsArgument() {
		buildChangeSet {
			tagDatabase 'monkey'
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof TagDatabaseChange
		assertEquals 'monkey', changes[0].tag
		assertNoOutput()
	}

	/**
	 * Test parsing a stop change with an empty parameter map.  In this case, we
	 * expect Liquibase to give us a default message.
	 */
	@Test
	void stopEmpty() {
		buildChangeSet {
			stop([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof StopChange
		assertEquals 'Stop command in changelog file', changes[0].message
		assertNoOutput()
	}

	/**
	 * Test parsing a stop change when the message is in the attributes.
	 */
	@Test
	void stopMessageInAttributes() {
		buildChangeSet {
			stop(message: 'Stop the refactoring. Just...stop.')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof StopChange
		assertEquals 'Stop the refactoring. Just...stop.', changes[0].message
		assertNoOutput()
	}

	/**
	 * Test parsing a stop change when the message is not in an attribute.
	 */
	@Test
	void stopMessageIsArgument() {
		buildChangeSet {
			stop 'Stop the refactoring. Just...stop.'
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof StopChange
		assertEquals 'Stop the refactoring. Just...stop.', changes[0].message
		assertNoOutput()
	}
}

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

import liquibase.change.core.AddColumnChange
import liquibase.change.core.ModifyDataTypeChange
import org.junit.Test
import static org.junit.Assert.*

import liquibase.change.ColumnConfig
import liquibase.change.core.RenameColumnChange
import liquibase.change.core.DropColumnChange
import liquibase.change.core.AlterSequenceChange
import liquibase.change.core.CreateTableChange
import liquibase.change.core.RenameTableChange
import liquibase.change.core.DropTableChange
import liquibase.change.core.CreateViewChange
import liquibase.change.core.RenameViewChange
import liquibase.change.core.DropViewChange
import liquibase.change.core.MergeColumnChange
import liquibase.change.core.CreateProcedureChange

/**
 * This class tests ChangeSet refactoring changes that alter table structure.
 * The tests make sure the DSL can parse each change correctly and handle all
 * options supported by Liquibase.  It does not worry about validating the
 * change itself (making sure required attributes are present for example),
 * that is done by Liquibase itself.
 */
class StructuralRefactoringTests extends ChangeSetTests {

	// add with no column or attribute
	/**
	 * Try creating an addColumn change with no attributes and an empty closure.
	 * Make sure the DSL doesn't try to make any assumptions.  It also validates
	 * our assumption that a Liquibase AddColumnChange always has a collection
	 * of columns, even if they are empty.
	 */
	@Test
	void addColumnEmpty() {
		buildChangeSet {
			addColumn([:]) {}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddColumnChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		def columns = changes[0].columns
		assertNotNull columns
		assertEquals 0, columns.size()
		assertNoOutput()
	}

	/**
	 * Test adding a column with a full set of attributes, and only one column,
	 * which does not have any constraints.  We don't worry about the contents
	 * of the column itself, as we do that when we test the ColumnDelegate.
	 */
	@Test
	void addColumnFull() {
		buildChangeSet {
			addColumn(catalogName: 'zoo', schemaName: 'animal', tableName: 'monkey') {
				column(name: 'monkey_status', type: 'varchar(98)')
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddColumnChange
		assertEquals 'zoo', changes[0].catalogName
		assertEquals 'animal', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		def columns = changes[0].columns
		assertNotNull columns
		assertEquals 1, columns.size()
		assertNoOutput()
	}

	/**
	 * Test adding a column with a full set of attributes, and two columns. We
	 * don't worry about the contents of the column, and we won't worry about
	 * columns with constraints, because that will be checked in the tests for
	 * the ColumnDelegate.
	 */
	@Test
	void addColumnFullWithTwoColumns() {
		buildChangeSet {
			addColumn(catalogName: 'zoo', schemaName: 'animal', tableName: 'monkey') {
				column(name: 'monkey_status', type: 'varchar(98)')
				column(name: 'monkey_business', type: 'varchar(98)')
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddColumnChange
		assertEquals 'zoo', changes[0].catalogName
		assertEquals 'animal', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		def columns = changes[0].columns
		assertNotNull columns
		assertEquals 2, columns.size()
		assertEquals 'monkey_status', columns[0].name
		assertEquals 'monkey_business', columns[1].name
		assertNoOutput()
	}

	/**
	 * Test parsing a renameColumn change when we have no attributes to make sure
	 * the DSL doesn't introduce any unintended defaults.
	 */
	@Test
	void renameColumnEmpty() {
		buildChangeSet {
			renameColumn([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof RenameColumnChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].oldColumnName
		assertNull changes[0].newColumnName
		assertNull changes[0].columnDataType
		assertNoOutput()
	}

	/**
	 * Test parsing a renameColumn change when we have all supported attributes.
	 */
	@Test
	void renameColumnFull() {
		buildChangeSet {
			renameColumn(catalogName: 'catalog',
							     schemaName: 'schema',
							     tableName: 'monkey',
							     oldColumnName: 'fail',
							     newColumnName: 'win',
							     columnDataType: 'varchar(9001)')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof RenameColumnChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'fail', changes[0].oldColumnName
		assertEquals 'win', changes[0].newColumnName
		assertEquals 'varchar(9001)', changes[0].columnDataType
		assertNoOutput()
	}

	/**
	 * Test parsing a dropColumn change with no attributes to make sure the DSL
	 * doesn't introduce any side effects.
	 */
	@Test
	void dropColumnEmpty() {
		buildChangeSet {
			dropColumn([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropColumnChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].columnName
		assertNoOutput()
	}

	/**
	 * Test parsing a dropColumn change with all supported attributes.
	 */
	@Test
	void dropColumnFull() {
		buildChangeSet {
			dropColumn(catalogName: 'catalog',
							   schemaName: 'schema',
							   tableName: 'monkey',
							   columnName: 'emotion')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropColumnChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'emotion', changes[0].columnName
		assertNoOutput()
	}

	/**
	 * Test parsing a createTable change when we have no attributes and an empty
	 * closure.  This just makes sure the DSL doesn't add any defaults.  We
	 * don't need to support no map or no closure because it makes no sense to
	 * have a createTable without at least a name and one column.
	 */
	@Test
	void createTableEmpty() {
		buildChangeSet {
			createTable([:]) {}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateTableChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tablespace
		assertNull changes[0].tableName
		assertNull changes[0].remarks

		def columns = changes[0].columns
		assertNotNull columns
		assertEquals 0, columns.size()
		assertNoOutput()
	}

	/**
	 * Test parsing a createTable change with all supported attributes and a
	 * couple of columns.
	 */
	@Test
	void createTableFull() {
		buildChangeSet {
			createTable(catalogName: 'catalog',
							    schemaName: 'schema',
							    tablespace: 'oracle_tablespace',
							    tableName: 'monkey',
							    remarks: 'angry') {
				column(name: 'status', type: 'varchar(100)')
				column(name: 'id', type: 'int')
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateTableChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'oracle_tablespace', changes[0].tablespace
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'angry', changes[0].remarks

		def columns = changes[0].columns
		assertNotNull columns
		assertEquals 2, columns.size()
		assertTrue columns[0] instanceof ColumnConfig
		assertEquals 'status', columns[0].name
		assertEquals 'varchar(100)', columns[0].type
		assertTrue columns[1] instanceof ColumnConfig
		assertEquals 'id', columns[1].name
		assertEquals 'int', columns[1].type
		assertNoOutput()
	}

	/**
	 * Test parsing a renameTable change when we have no attributes to make sure
	 * we don't get any unintended defautls.
	 */
	@Test
	void renameTableEmpty() {
		buildChangeSet {
			renameTable([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof RenameTableChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].oldTableName
		assertNull changes[0].newTableName
		assertNoOutput()
	}

	/**
	 * Test parsing a renameTable change with all supported attributes.
	 */
	@Test
	void renameTableFull() {
		buildChangeSet {
			renameTable(catalogName: 'catalog',
							    schemaName: 'schema',
							    oldTableName: 'fail_table',
							    newTableName: 'win_table')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof RenameTableChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'fail_table', changes[0].oldTableName
		assertEquals 'win_table', changes[0].newTableName
		assertNoOutput()
	}

	/**
	 * Test parsing a dropTable change with no attributes to make sure the DSL
	 * doesn't introduce any unexpected changes.
	 */
	@Test
	void dropTableEmpty() {
		buildChangeSet {
			dropTable([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropTableChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].cascadeConstraints
		assertNoOutput()
	}

	/**
	 * Test parsing a dropTable change with all supported attributes.
	 */
	@Test
	void dropTableFull() {
		buildChangeSet {
			dropTable(catalogName: 'catalog',
							  schemaName: 'schema',
							  tableName: 'fail_table',
							  cascadeConstraints: true)
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropTableChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'fail_table', changes[0].tableName
		assertTrue changes[0].cascadeConstraints
		assertNoOutput()
	}

	/**
	 * Test parsing a createView change with an empty attribute map and an empty
	 * closure to make sure the DSL doesn't introduce any defaults.
	 */
	@Test
	void createViewEmpty() {
		buildChangeSet {
			createView([:]) {}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateViewChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].viewName
		assertNull changes[0].replaceIfExists
		assertNull changes[0].selectQuery
		assertNoOutput()
	}

	/**
	 * Test parsing a createView change with all supported attributes and a
	 * closure.  Since createView changes need to have at least a name and
	 * query, we don't need to test for sql by itself.
	 */
	@Test
	void createViewFull() {
		buildChangeSet {
			createView(catalogName: 'catalog',
							   schemaName: 'schema',
							   viewName: 'monkey_view',
							   replaceIfExists: true) {
				"SELECT * FROM monkey WHERE state='angry'"
			}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateViewChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey_view', changes[0].viewName
		assertTrue changes[0].replaceIfExists
		assertEquals "SELECT * FROM monkey WHERE state='angry'", changes[0].selectQuery
		assertNoOutput()
	}

	/**
	 * Test parsing a renameView change when we have no attributes to make sure
	 * we don't get any unintended defaults.
	 */
	@Test
	void renameViewEmpty() {
		buildChangeSet {
			renameView([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof RenameViewChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].oldViewName
		assertNull changes[0].newViewName
		assertNoOutput()
	}

	/**
	 * Test parsing a renameView change with all the supported attributes.
	 */
	@Test
	void renameViewFull() {
		buildChangeSet {
			renameView(catalogName: 'catalog',
							   schemaName: 'schema',
							   oldViewName: 'fail_view',
							   newViewName: 'win_view')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof RenameViewChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'fail_view', changes[0].oldViewName
		assertEquals 'win_view', changes[0].newViewName
		assertNoOutput()
	}

	/**
	 * Test parsing a dropView change with no attributes to make sure the DSL
	 * doesn't introduce any unexpected defaults.
	 */
	@Test
	void dropViewEmpty() {
		buildChangeSet {
			dropView([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropViewChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].viewName
		assertNoOutput()
	}

	/**
	 * Test parsing a dropView change with all supported options
	 */
	@Test
	void dropViewFull() {
		buildChangeSet {
			dropView(catalogName: 'catalog',
							 schemaName: 'schema',
							 viewName: 'fail_view')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropViewChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'fail_view', changes[0].viewName
		assertNoOutput()
	}

	/**
	 * Test parsing a mergeColumn change when there are no attributes to make sure
	 * the DSL doesn't introduce unintended defaults.
	 */
	@Test
	void mergeColumnsEmpty() {
		buildChangeSet {
			mergeColumns([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof MergeColumnChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].column1Name
		assertNull changes[0].column2Name
		assertNull changes[0].finalColumnName
		assertNull changes[0].finalColumnType
		assertNull changes[0].joinString
		assertNoOutput()
	}

	/**
	 * Test parsing a mergeColumn change when we have all supported attributes.
	 */
	@Test
	void mergeColumnsFull() {
		buildChangeSet {
			mergeColumns(catalogName: 'catalog',
							     schemaName: 'schema',
							     tableName: 'table',
							     column1Name: 'first_name',
							     column2Name: 'last_name',
							     finalColumnName: 'full_name',
							     finalColumnType: 'varchar(99)',
							     joinString: ' ')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof MergeColumnChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'table', changes[0].tableName
		assertEquals 'first_name', changes[0].column1Name
		assertEquals 'last_name', changes[0].column2Name
		assertEquals 'full_name', changes[0].finalColumnName
		assertEquals 'varchar(99)', changes[0].finalColumnType
		assertEquals ' ', changes[0].joinString
		assertNoOutput()
	}

	/**
	 * Test parsing a mergeColumn change when there are no attributes to make sure
	 * the DSL doesn't introduce unintended defaults.
	 */
	@Test
	void modifyDataTypeEmpty() {
		buildChangeSet {
			modifyDataType([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof ModifyDataTypeChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].columnName
		assertNull changes[0].newDataType
		assertNoOutput()
	}

	/**
	 * Test parsing a mergeColumn change when we have all supported attributes.
	 */
	@Test
	void modifyDataTypeFull() {
		buildChangeSet {
			modifyDataType(catalogName: 'catalog',
							schemaName: 'schema',
							tableName: 'table',
							columnName: 'first_name',
							newDataType: 'varchar(99)')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof ModifyDataTypeChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'table', changes[0].tableName
		assertEquals 'first_name', changes[0].columnName
		assertEquals 'varchar(99)', changes[0].newDataType
		assertNoOutput()
	}

	/**
	 * Test parsing a createProcedure change when we have an empty map and
	 * closure to make sure the DSL doesn't try to set any defaults.
	 */
	@Test
	void createProcedureEmpty() {
		buildChangeSet {
			createProcedure ([:]) {}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertNull changes[0].comments
		assertNull changes[0].procedureBody
		assertNoOutput()
	}

	/**
	 * test parsing a createProcedure change when we have no attributes
	 * just the body in a closure.  Since the only supported attribute is for
	 * comments, this will be common.
	 */
	@Test
	void createProcedureClosureOnly() {
		def sql = """\
CREATE OR REPLACE PROCEDURE testMonkey
IS
BEGIN
 -- do something with the monkey
END;"""
		buildChangeSet {
			createProcedure { sql }
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateProcedureChange
		assertNull changes[0].comments
		assertEquals sql, changes[0].procedureBody
		assertNoOutput()
	}

	/**
	 * Test parsing a createProcedure change when we have no attributes,
	 * just the procedure body as a string.  Since the only supported attribute
	 * is for comments, this will be common.
	 */
	@Test
	void createProcedureSqlOnlyAsString() {
		def sql = """\
CREATE OR REPLACE PROCEDURE testMonkey
IS
BEGIN
 -- do something with the monkey
END;"""
		buildChangeSet {
			createProcedure sql
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateProcedureChange
		assertNull changes[0].comments
		assertEquals sql, changes[0].procedureBody
		assertNoOutput()
	}

	/**
	 * Test parsing a createProcedure change when we have both comments
	 * and SQL.
	 */
	@Test
	void createProcedureFull() {
		def sql = """\
CREATE OR REPLACE PROCEDURE testMonkey
IS
BEGIN
 -- do something with the monkey
END;"""
		buildChangeSet {
			createProcedure(comments: 'someComments') { sql }
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateProcedureChange
		assertEquals 'someComments', changes[0].comments
		assertEquals sql, changes[0].procedureBody
		assertNoOutput()
	}

	/**
	 * Test parsing a createStoredProcedure change when we have an empty map and
	 * closure to make sure the DSL doesn't try to set any defaults.  This is
	 * deprecated, so expect a warning.
	 */
	@Test
	void createStoredProcedureEmpty() {
		buildChangeSet {
			createStoredProcedure ([:]) {}
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertNull changes[0].comments
		assertNull changes[0].procedureBody
		assertPrinted("createStoredProcedure has been deprecated")
	}

	/**
	 * test parsing a createStoredProcedure change when we have no attributes
	 * just the body in a closure.  Since the only supported attribute is for
	 * comments, this will be common.  This has been deprecated, so expect a
	 * warning.
	 */
	@Test
	void createStoredProcedureClosureOnly() {
		def sql = """\
CREATE OR REPLACE PROCEDURE testMonkey
IS
BEGIN
 -- do something with the monkey
END;"""
		buildChangeSet {
			createStoredProcedure { sql }
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateProcedureChange
		assertNull changes[0].comments
		assertEquals sql, changes[0].procedureBody
		assertPrinted("createStoredProcedure has been deprecated")
	}

	/**
	 * Test parsing a createStoredProcedure change when we have no attributes,
	 * just the procedure body as a string.  Since the only supported attribute
	 * is for comments, this will be common.  This has been deprecated, so expect
	 * a warning.
	 */
	@Test
	void createStoredProcedureSqlOnlyAsString() {
		def sql = """\
CREATE OR REPLACE PROCEDURE testMonkey
IS
BEGIN
 -- do something with the monkey
END;"""
		buildChangeSet {
			createStoredProcedure sql
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateProcedureChange
		assertNull changes[0].comments
		assertEquals sql, changes[0].procedureBody
		assertPrinted("createStoredProcedure has been deprecated")
	}

	/**
	 * Test parsing a createStoredProcedure change when we have both comments
	 * and SQL. This has been deprecated, so expect a warning.
	 */
	@Test
	void createStoredProcedureFull() {
		def sql = """\
CREATE OR REPLACE PROCEDURE testMonkey
IS
BEGIN
 -- do something with the monkey
END;"""
		buildChangeSet {
			createStoredProcedure(comments: 'someComments') { sql }
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateProcedureChange
		assertEquals 'someComments', changes[0].comments
		assertEquals sql, changes[0].procedureBody
		assertPrinted("createStoredProcedure has been deprecated")
	}
}
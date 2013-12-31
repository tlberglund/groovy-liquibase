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

import liquibase.change.core.AddLookupTableChange
import liquibase.change.core.AddNotNullConstraintChange
import liquibase.change.core.AlterSequenceChange;
import liquibase.change.core.DropNotNullConstraintChange
import liquibase.change.core.AddUniqueConstraintChange
import liquibase.change.core.DropUniqueConstraintChange
import liquibase.change.core.CreateSequenceChange
import liquibase.change.core.DropSequenceChange
import liquibase.change.core.AddAutoIncrementChange
import liquibase.change.core.AddDefaultValueChange
import liquibase.change.core.DropDefaultValueChange
import liquibase.statement.DatabaseFunction

/**
 * This is one of several classes that test the creation of refactoring changes
 * for ChangeSets. This particular class tests changes that deal with data
 * quality.
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
 */
class DataQualityRefactoringTests extends ChangeSetTests {

	/**
	 * Parse an addLookupTable change with no attributes to make sure the DSL
	 * doesn't make up any defaults.
	 */
	@Test
	void addLookupTableEmpty() {
		buildChangeSet {
			addLookupTable([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddLookupTableChange
		assertNull changes[0].existingTableName
		assertNull changes[0].existingTableCatalogName
		assertNull changes[0].existingTableSchemaName
		assertNull changes[0].existingColumnName
		assertNull changes[0].newTableName
		assertNull changes[0].newTableCatalogName
		assertNull changes[0].newTableSchemaName
		assertNull changes[0].newColumnName
		assertNull changes[0].newColumnDataType
		assertNull changes[0].constraintName
		assertNoOutput()
	}

	/**
	 * Parse an addLookupTable change with all supported attributes set.
	 */
	@Test
	void addLookupTableFull() {
		buildChangeSet {
			addLookupTable(existingTableName: 'monkey',
							       existingTableCatalogName: 'old_catalog',
							       existingTableSchemaName: 'old_schema',
							       existingColumnName: 'emotion',
							       newTableName: 'monkey_emotion',
							       newTableCatalogName: 'new_catalog',
							       newTableSchemaName: 'new_schema',
							       newColumnName: 'emotion_display',
							       newColumnDataType: 'varchar(50)',
							       constraintName: 'fk_monkey_emotion'
			)
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddLookupTableChange
		assertEquals 'monkey', changes[0].existingTableName
		assertEquals 'old_catalog', changes[0].existingTableCatalogName
		assertEquals 'old_schema', changes[0].existingTableSchemaName
		assertEquals 'emotion', changes[0].existingColumnName
		assertEquals 'monkey_emotion', changes[0].newTableName
		assertEquals 'new_catalog', changes[0].newTableCatalogName
		assertEquals 'new_schema', changes[0].newTableSchemaName
		assertEquals 'emotion_display', changes[0].newColumnName
		assertEquals 'varchar(50)', changes[0].newColumnDataType
		assertEquals 'fk_monkey_emotion', changes[0].constraintName
		assertNoOutput()
	}

	/**
	 * Parse an addNotNullConstraint with no attributes to make sure the DSL
	 * doesn't make up any defaults.
	 */
	@Test
	void addNotNullConstraintEmpty() {
		buildChangeSet {
			addNotNullConstraint([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddNotNullConstraintChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].columnName
		assertNull changes[0].defaultNullValue
		assertNull changes[0].columnDataType
		assertNoOutput()
	}

	/**
	 * Parse an addNotNullConstraint with all supported options set.
	 */
	@Test
	void addNotNullConstraintFull() {
		buildChangeSet {
			addNotNullConstraint(catalogName: 'catalog',
							             schemaName: 'schema',
							             tableName: 'monkey',
							             columnName: 'emotion',
							             defaultNullValue: 'angry',
							             columnDataType: 'varchar(75)')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddNotNullConstraintChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'emotion', changes[0].columnName
		assertEquals 'angry', changes[0].defaultNullValue
		assertEquals 'varchar(75)', changes[0].columnDataType
		assertNoOutput()
	}

	/**
	 * Test parsing a dropNotNullConstraint change with no attributes to make sure
	 * the DSL doesn't introduce unexpected defaults.
	 */
	@Test
	void dropNotNullConstraintEmpty() {
		buildChangeSet {
			dropNotNullConstraint([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropNotNullConstraintChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].columnName
		assertNull changes[0].columnDataType
		assertNoOutput()
	}

	/**
	 * Test parsing a dropNotNullConstraint with all supported attributes.
	 */
	@Test
	void dropNotNullConstraintFull() {
		buildChangeSet {
			dropNotNullConstraint(catalogName: 'catalog',
							              schemaName: 'schema',
							              tableName: 'monkey',
							              columnName: 'emotion',
							              columnDataType: 'varchar(75)')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropNotNullConstraintChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'emotion', changes[0].columnName
		assertEquals 'varchar(75)', changes[0].columnDataType
		assertNoOutput()
	}

	/**
	 * Test parsing an addUniqueConstraint change with no attributes to make sure
	 * the DSL doesn't create any default values.
	 */
	@Test
	void addUniqueConstraintEmpty() {
		buildChangeSet {
			addUniqueConstraint([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddUniqueConstraintChange
		assertNull changes[0].tablespace
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].columnNames
		assertNull changes[0].constraintName
		assertNull changes[0].deferrable
		assertNull changes[0].initiallyDeferred
		assertNull changes[0].disabled
		assertNoOutput()
	}

	/**
	 * Test parsing an addUniqueConstraint change when we have all supported
	 * options.  There are 3 booleans here, so to isolate the attributes, this
	 * test will only set deferrable to true.
	 */
	@Test
	void addUniqueConstraintFullDeferrable() {
		buildChangeSet {
			addUniqueConstraint(tablespace: 'tablespace',
							catalogName: 'catalog',
							schemaName: 'schema',
							tableName: 'monkey',
							columnNames: 'species, emotion',
							constraintName: 'unique_constraint',
							deferrable: true,
							initiallyDeferred: false,
							disabled: false)
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddUniqueConstraintChange
		assertEquals 'tablespace', changes[0].tablespace
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'species, emotion', changes[0].columnNames
		assertEquals 'unique_constraint', changes[0].constraintName
		assertTrue changes[0].deferrable
		assertFalse changes[0].initiallyDeferred
		assertFalse changes[0].disabled
		assertNoOutput()
	}

	/**
	 * Test parsing an addUniqueConstraint change when we have all supported
	 * options.  There are 3 booleans here, so to isolate the attributes, this
	 * test will only set initiallyDeferred to true.
	 */
	@Test
	void addUniqueConstraintFullDeferred() {
		buildChangeSet {
			addUniqueConstraint(tablespace: 'tablespace',
							catalogName: 'catalog',
							schemaName: 'schema',
							tableName: 'monkey',
							columnNames: 'species, emotion',
							constraintName: 'unique_constraint',
							deferrable: false,
							initiallyDeferred: true,
							disabled: false)
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddUniqueConstraintChange
		assertEquals 'tablespace', changes[0].tablespace
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'species, emotion', changes[0].columnNames
		assertEquals 'unique_constraint', changes[0].constraintName
		assertFalse changes[0].deferrable
		assertTrue changes[0].initiallyDeferred
		assertFalse changes[0].disabled
		assertNoOutput()
	}

	/**
	 * Test parsing an addUniqueConstraint change when we have all supported
	 * options.  There are 3 booleans here, so to isolate the attributes, this
	 * test will only set deferrable to true.
	 */
	@Test
	void addUniqueConstraintFullDisabled() {
		buildChangeSet {
			addUniqueConstraint(tablespace: 'tablespace',
							catalogName: 'catalog',
							schemaName: 'schema',
							tableName: 'monkey',
							columnNames: 'species, emotion',
							constraintName: 'unique_constraint',
							deferrable: false,
							initiallyDeferred: false,
							disabled: true)
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddUniqueConstraintChange
		assertEquals 'tablespace', changes[0].tablespace
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'species, emotion', changes[0].columnNames
		assertEquals 'unique_constraint', changes[0].constraintName
		assertFalse changes[0].deferrable
		assertFalse changes[0].initiallyDeferred
		assertTrue changes[0].disabled
		assertNoOutput()
	}

	/**
	 * Test parsing a dropUniqueConstraint change with no attributes to make sure
	 * the DSL doesn't introduce any unexpected defaults.
	 */
	@Test
	void dropUniqueConstraintEmpty() {
		buildChangeSet {
			dropUniqueConstraint([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropUniqueConstraintChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].constraintName
		assertNull changes[0].uniqueColumns
		assertNoOutput()
	}

	/**
	 * Test parsing a dropUniqueConstraint change with all supported options
	 */
	@Test
	void dropUniqueConstraintFull() {
		buildChangeSet {
			dropUniqueConstraint(catalogName: 'catalog',
							             schemaName: 'schema',
							             tableName: 'table',
							             constraintName: 'unique_constraint',
			                     uniqueColumns: 'unique_column')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropUniqueConstraintChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'table', changes[0].tableName
		assertEquals 'unique_constraint', changes[0].constraintName
		assertEquals 'unique_column', changes[0].uniqueColumns
		assertNoOutput()
	}

	/**
	 * Test parsing a createSequence change with no attributes to make sure the
	 * DSL doesn't create any defaults.
	 */
	@Test
	void createSequenceEmpty() {
		buildChangeSet {
			createSequence([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateSequenceChange
		assertNull changes[0].sequenceName
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].startValue
		assertNull changes[0].incrementBy
		assertNull changes[0].minValue
		assertNull changes[0].maxValue
		assertNull changes[0].ordered
		assertNull changes[0].cycle
		assertNoOutput()
	}

	/**
	 * Test parsing a createSequence change with all attributes present to make
	 * sure they all go to the right place.
	 */
	@Test
	void createSequenceFull() {
		buildChangeSet {
			createSequence(catalogName: 'catalog',
							       sequenceName: 'sequence',
							       schemaName: 'schema',
							       startValue: 8,
							       incrementBy: 42,
							       minValue: 7,
							       maxValue: 6.023E24,
							       ordered: true,
							       cycle: false)
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof CreateSequenceChange
		assertEquals 'sequence', changes[0].sequenceName
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 42G, changes[0].incrementBy
		assertEquals 7G, changes[0].minValue
		assertEquals 6023000000000000000000000, changes[0].maxValue
		assertEquals 8G, changes[0].startValue
		assertTrue changes[0].ordered
		assertFalse changes[0].cycle
		assertNoOutput()
	}

	/**
	 * Test parsing an alterSequence change with no attributes to make sure the
	 * DSL doesn't create default values
	 */
	@Test
	void alterSequenceEmpty() {
		buildChangeSet {
			alterSequence([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AlterSequenceChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].sequenceName
		assertNull changes[0].incrementBy
		assertNull changes[0].minValue
		assertNull changes[0].maxValue
		assertNull changes[0].ordered // it is an Object and can be null.
		assertNoOutput()
	}

	/**
	 * Test parsing an alterSequence change with all supported attributes
	 * present.
	 */
	@Test
	void alterSequenceFull() {
		buildChangeSet {
			alterSequence(catalogName: 'catalog',
							schemaName: 'schema',
							sequenceName: 'seq',
							incrementBy: 314,
							minValue: 300,
							maxValue: 400,
							ordered: true)
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AlterSequenceChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'seq', changes[0].sequenceName
		assertEquals 314G, changes[0].incrementBy
		assertEquals 300G, changes[0].minValue
		assertEquals 400G, changes[0].maxValue
		assertTrue changes[0].ordered
		assertNoOutput()
	}

	/**
	 * Test parsing a dropSequence change with no attributes to make sure the DSL
	 * doesn't introduce unexpected defaults.
	 */
	@Test
	void dropSequenceEmpty() {
		buildChangeSet {
			dropSequence([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropSequenceChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].sequenceName
		assertNoOutput()
	}

	/**
	 * Test parsing a dropSequence change with all supported attributes.
	 */
	@Test
	void dropSequenceFull() {
		buildChangeSet {
			dropSequence(catalogName: 'catalog',
							     schemaName: 'schema',
							     sequenceName: 'sequence')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropSequenceChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'sequence', changes[0].sequenceName
		assertNoOutput()
	}

	/**
	 * Test the addAutoIncrement changeSet with no attributes to make sure the
	 * DSL doesn't try to set any defaults.
	 */
	@Test
	void addAutoIncrementEmpty() {
		buildChangeSet {
			addAutoIncrement([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddAutoIncrementChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].columnName
		assertNull changes[0].columnDataType
		assertNull changes[0].startWith
		assertNull changes[0].incrementBy
		assertNoOutput()
	}

	/**
	 * Test the addAutoIncrement change set.
	 */
	@Test
	void addAutoIncrementFull() {
		buildChangeSet {
			addAutoIncrement(catalogName: 'catalog',
							         schemaName: 'schema',
							         tableName: 'monkey',
							         columnName: 'angry',
							         columnDataType: 'boolean',
							         startWith: 10,
							         incrementBy: 5)
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddAutoIncrementChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'angry', changes[0].columnName
		assertEquals 'boolean', changes[0].columnDataType
		assertEquals 10G, changes[0].startWith
		assertEquals 5G, changes[0].incrementBy
		assertNoOutput()
	}

	/**
	 * Validate the creation of an addDefaultValue change when there are no
	 * attributes set.  Make sure the DSL didn't make up values.
	 */
	@Test
	void addDefaultValueEmpty() {
		buildChangeSet {
			addDefaultValue([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddDefaultValueChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].columnName
		assertNull changes[0].columnDataType
		assertNull changes[0].defaultValue
		assertNull changes[0].defaultValueBoolean // it's an Object, so it can be null
		assertNull changes[0].defaultValueComputed
		assertNull changes[0].defaultValueDate
		assertNull changes[0].defaultValueNumeric
		assertNull changes[0].defaultValueSequenceNext
		assertNoOutput()
	}

	/**
	 * Test the creation of an addDefaultValue change when all attributes are set.
	 * Remember, the DSL doesn't do any validation - Liquibase does.  We only
	 * care that the DSL sets the proper values in the Liquibase object from the
	 * attribute map.
	 */
	@Test
	void addDefaultValueFull() {
		buildChangeSet {
			addDefaultValue(catalogName: 'catalog',
							        schemaName: 'schema',
							        tableName: 'monkey',
							        columnName: 'strength',
							        columnDataType: 'int',
							        defaultValue: 'extremely',
							        defaultValueBoolean: true,
							        defaultValueComputed: 'max',
							        defaultValueDate: '20101109T130400Z',
							        defaultValueNumeric: '2.718281828459045',
							        defaultValueSequenceNext: 'sequence')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddDefaultValueChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'strength', changes[0].columnName
		assertEquals 'int', changes[0].columnDataType
		assertEquals 'extremely', changes[0].defaultValue
		assertTrue changes[0].defaultValueBoolean
		assertEquals new DatabaseFunction('max'), changes[0].defaultValueComputed
		assertEquals '20101109T130400Z', changes[0].defaultValueDate
		assertEquals '2.718281828459045', changes[0].defaultValueNumeric
		assertEquals 'sequence', changes[0].defaultValueSequenceNext.value
		assertNoOutput()
	}

	/**
	 * Test parsing a dropDefaultValue change with no attributes to make sure the
	 * DSL doesn't introduce any unexpected defaults.
	 */
	@Test
	void dropDefaultValueEmpty() {
		buildChangeSet {
			dropDefaultValue([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropDefaultValueChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].columnName
		assertNull changes[0].columnDataType
		assertNoOutput()
	}

	/**
	 * Test parsing a dropDefaultValue change with all supported attributes
	 */
	@Test
	void dropDefaultValueFull() {
		buildChangeSet {
			dropDefaultValue(catalogName: 'catalog',
							         schemaName: 'schema',
							         tableName: 'monkey',
							         columnName: 'emotion',
							         columnDataType: 'varchar')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropDefaultValueChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'emotion', changes[0].columnName
		assertEquals 'varchar', changes[0].columnDataType
		assertNoOutput()
	}
}

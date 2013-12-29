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

import liquibase.change.core.DropAllForeignKeyConstraintsChange
import org.junit.Ignore
import org.junit.Test
import static org.junit.Assert.*

import liquibase.change.core.AddForeignKeyConstraintChange
import liquibase.change.core.DropForeignKeyConstraintChange
import liquibase.change.core.AddPrimaryKeyChange
import liquibase.change.core.DropPrimaryKeyChange


/**
 * This class tests ChangeSet refactoring changes that deal with referential
 * integrity.  The tests make sure the DSL can parse each change correctly and
 * handle all options supported by Liquibase.  It does not worry about
 * validating the change itself (making sure required attributes are present for
 * example), that is done by Liquibase itself.
 */
class ReferentialIntegrityRefactoringTests extends ChangeSetTests {

	/**
	 * Build an addForeignKeyConstraint with no attributes to make sure the DSL
	 * doesn't make up defaults.
	 */
	@Test
	void addForeignKeyConstraintEmpty() {
		buildChangeSet {
			addForeignKeyConstraint([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddForeignKeyConstraintChange
		assertNull changes[0].constraintName
		assertNull changes[0].baseTableName
		assertNull changes[0].baseTableCatalogName
		assertNull changes[0].baseTableSchemaName
		assertNull changes[0].baseColumnNames
		assertNull changes[0].referencedTableName
		assertNull changes[0].referencedTableCatalogName
		assertNull changes[0].referencedTableSchemaName
		assertNull changes[0].referencedColumnNames
		assertNull changes[0].deferrable
		assertNull changes[0].initiallyDeferred
		assertNull changes[0].onDelete
		assertNull changes[0].onUpdate
		assertNoOutput()
	}

	/**
	 * Make an addForeignKeyConstraint with all attributes set to make sure the
	 * right values go to the right places.
	 */
	@Test
	void addForeignKeyConstraintFull() {
		buildChangeSet {
			addForeignKeyConstraint(constraintName: 'fk_monkey_emotion',
							                baseTableName: 'monkey',
							                baseTableCatalogName: 'base_catalog',
							                baseTableSchemaName: 'base_schema',
							                baseColumnNames: 'emotion_id',
							                referencedTableName: 'emotions',
							                referencedTableCatalogName: 'referenced_catalog',
							                referencedTableSchemaName: 'referenced_schema',
							                referencedColumnNames: 'id',
							                deferrable: true,
							                initiallyDeferred: false,
							                onDelete: 'RESTRICT',
							                onUpdate: 'CASCADE')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddForeignKeyConstraintChange
		assertEquals 'fk_monkey_emotion', changes[0].constraintName
		assertEquals 'monkey', changes[0].baseTableName
		assertEquals 'base_catalog', changes[0].baseTableCatalogName
		assertEquals 'base_schema', changes[0].baseTableSchemaName
		assertEquals 'emotion_id', changes[0].baseColumnNames
		assertEquals 'emotions', changes[0].referencedTableName
		assertEquals 'referenced_catalog', changes[0].referencedTableCatalogName
		assertEquals 'referenced_schema', changes[0].referencedTableSchemaName
		assertEquals 'id', changes[0].referencedColumnNames
		assertTrue changes[0].deferrable
		assertFalse changes[0].initiallyDeferred
		assertEquals 'RESTRICT', changes[0].onDelete
		assertEquals 'CASCADE', changes[0].onUpdate
		assertNoOutput()
	}

	/**
	 * Liquibase has an undocumented attribute {@code deleteCascade}, which does
	 * the same thing as {@code onDelete: 'CASCADE'}, so let's make sure it works.
	 * Since it is undocumented, we may want to deprecate this option.  We can't
	 * just delete it because older versions of the DSL supported it, but we can
	 * make sure we get the deprecation warning on stdout.
	 */
	@Test
	void addForeignKeyConstraintWithDeleteCascadeProperty() {
		buildChangeSet {
			addForeignKeyConstraint(constraintName: 'fk_monkey_emotion',
							                baseTableName: 'monkey',
							                baseTableCatalogName: 'base_catalog',
							                baseTableSchemaName: 'base_schema',
							                baseColumnNames: 'emotion_id',
							                referencedTableName: 'emotions',
							                referencedTableCatalogName: 'referenced_catalog',
							                referencedTableSchemaName: 'referenced_schema',
							                referencedColumnNames: 'id',
							                deferrable: false,
							                initiallyDeferred: true,
							                deleteCascade: true,
							                onUpdate: 'RESTRICT')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddForeignKeyConstraintChange
		assertEquals 'fk_monkey_emotion', changes[0].constraintName
		assertEquals 'monkey', changes[0].baseTableName
		assertEquals 'base_catalog', changes[0].baseTableCatalogName
		assertEquals 'base_schema', changes[0].baseTableSchemaName
		assertEquals 'emotion_id', changes[0].baseColumnNames
		assertEquals 'emotions', changes[0].referencedTableName
		assertEquals 'referenced_catalog', changes[0].referencedTableCatalogName
		assertEquals 'referenced_schema', changes[0].referencedTableSchemaName
		assertEquals 'id', changes[0].referencedColumnNames
		assertFalse changes[0].deferrable
		assertTrue changes[0].initiallyDeferred
		assertEquals 'CASCADE', changes[0].onDelete // set by deleteCascade: true
		assertEquals 'RESTRICT', changes[0].onUpdate
		assertPrinted("Warning: addForeignKeyConstraint's deleteCascade parameter has been deprecated")
	}

	/**
	 * Test parsing a dropForeignKeyConstraint change with no attributes to make
	 * sure the DSL doesn't introduce unexpected defaults.
	 */
	@Test
	void dropForeignKeyConstraintEmpty() {
		buildChangeSet {
			dropForeignKeyConstraint([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropForeignKeyConstraintChange
		assertNull changes[0].baseTableCatalogName
		assertNull changes[0].baseTableSchemaName
		assertNull changes[0].baseTableName
		assertNull changes[0].constraintName
		assertNoOutput()
	}

	/**
	 * Test parsing a dropForeignKeyConstraint with all supported options.
	 */
	@Test
	void dropForeignKeyConstraintFull() {
		buildChangeSet {
			dropForeignKeyConstraint(baseTableCatalogName: 'catalog',
							                 baseTableSchemaName: 'schema',
							                 baseTableName: 'monkey',
							                 constraintName: 'fk_monkey_emotion')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropForeignKeyConstraintChange
		assertEquals 'catalog', changes[0].baseTableCatalogName
		assertEquals 'schema', changes[0].baseTableSchemaName
		assertEquals 'monkey', changes[0].baseTableName
		assertEquals 'fk_monkey_emotion', changes[0].constraintName
		assertNoOutput()
	}

	/**
	 * Test parsing a dropAllForeignKeyConstraints change with no attributes to
	 * make sure the DSL doesn't introduce any defaults..
	 */
	@Test
	void dropAllForeignKeyConstraintsEmpty() {
		buildChangeSet {
			dropAllForeignKeyConstraints([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropAllForeignKeyConstraintsChange
		assertNull changes[0].baseTableCatalogName
		assertNull changes[0].baseTableSchemaName
		assertNull changes[0].baseTableName
		assertNoOutput()
	}

	/**
	 * Test parsing a dropAllForeignKeyConstraints change with all supported
	 * attributes.
	 */
	@Test
	void dropAllForeignKeyConstraintsFull() {
		buildChangeSet {
			dropAllForeignKeyConstraints(baseTableCatalogName: 'catalog',
							                     baseTableSchemaName: 'schema',
							                     baseTableName: 'monkey')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropAllForeignKeyConstraintsChange
		assertEquals 'catalog', changes[0].baseTableCatalogName
		assertEquals 'schema', changes[0].baseTableSchemaName
		assertEquals 'monkey', changes[0].baseTableName
		assertNoOutput()
	}

	/**
	 * Test parsing an addPrimaryKey change with no attributes to make sure the
	 * DSL doesn't make up any defaults.
	 */
	@Test
	void addPrimaryKeyEmpty() {
		buildChangeSet {
			addPrimaryKey([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddPrimaryKeyChange
		assertNull changes[0].constraintName
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].tablespace
		assertNull changes[0].columnNames
		assertNoOutput()
	}

	/**
	 * Test parsing an addPrimaryKey change with all supported attributes set.
	 */
	@Test
	void addPrimaryKeyFull() {
		buildChangeSet {
			addPrimaryKey(catalogName: 'catalog',
							      schemaName: 'schema',
							      tableName: 'monkey',
							      columnNames: 'id',
							      constraintName: 'pk_monkey',
							      tablespace: 'tablespace')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof AddPrimaryKeyChange
		assertEquals 'pk_monkey', changes[0].constraintName
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'tablespace', changes[0].tablespace
		assertEquals 'id', changes[0].columnNames
		assertNoOutput()
	}

	/**
	 * Test parsing a dropPrimaryKey change with no attributes to make sure the
	 * DSL doesn't introduce any unexpected defaults.
	 */
	@Test
	void dropPrimaryKeyEmpty() {
		buildChangeSet {
			dropPrimaryKey([:])
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropPrimaryKeyChange
		assertNull changes[0].catalogName
		assertNull changes[0].schemaName
		assertNull changes[0].tableName
		assertNull changes[0].constraintName
		assertNoOutput()
	}

	/**
	 * Test parsing a dropPrimaryKey change with all supported attributes.
	 */
	@Test
	void dropPrimaryKeyFull() {
		buildChangeSet {
			dropPrimaryKey(catalogName: 'catalog',
							       schemaName: 'schema',
							       tableName: 'monkey',
							       constraintName: 'pk_monkey')
		}

		def changes = changeSet.changes
		assertNotNull changes
		assertEquals 1, changes.size()
		assertTrue changes[0] instanceof DropPrimaryKeyChange
		assertEquals 'catalog', changes[0].catalogName
		assertEquals 'schema', changes[0].schemaName
		assertEquals 'monkey', changes[0].tableName
		assertEquals 'pk_monkey', changes[0].constraintName
		assertNoOutput()
	}
}

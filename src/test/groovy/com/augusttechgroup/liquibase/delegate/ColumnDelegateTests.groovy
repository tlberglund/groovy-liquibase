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

import liquibase.change.ColumnConfig
import liquibase.statement.DatabaseFunction
import liquibase.statement.SequenceCurrentValueFunction
import liquibase.statement.SequenceNextValueFunction
import org.junit.Test
import static org.junit.Assert.*
import java.sql.Timestamp
import liquibase.change.core.LoadDataColumnConfig
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.DatabaseChangeLog
import java.text.SimpleDateFormat

/**
 * Test class for the {@link ColumnDelegate}.  As usual, we're only verifying
 * that we can pass things to Liquibase correctly. We check all attributes that
 * are known at this time - note that several are undocumented.
 *
 * @author Tim Berglund
 * @author Steven C. Saliman
 */
class ColumnDelegateTests {
  def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

	/**
	 * Build a column with no attributes and no closure to make sure we don't
	 * introduce any unintended defaults.
	 */
	@Test
	void oneColumnEmptyNoClosure() {
		def delegate = buildColumnDelegate(ColumnConfig.class) {
			column([:])
		}

		assertNull delegate.whereClause
		assertEquals 1, delegate.columns.size()
		assertTrue delegate.columns[0] instanceof ColumnConfig
		assertNull delegate.columns[0].name
		assertNull delegate.columns[0].type
		assertNull delegate.columns[0].value
		assertNull delegate.columns[0].valueNumeric
		assertNull delegate.columns[0].valueBoolean
		assertNull delegate.columns[0].valueDate
		assertNull delegate.columns[0].valueComputed
		assertNull delegate.columns[0].valueSequenceNext
		assertNull delegate.columns[0].valueSequenceCurrent
		assertNull delegate.columns[0].valueBlobFile
		assertNull delegate.columns[0].valueClobFile
		assertNull delegate.columns[0].defaultValue
		assertNull delegate.columns[0].defaultValueNumeric
		assertNull delegate.columns[0].defaultValueDate
		assertNull delegate.columns[0].defaultValueBoolean
		assertNull delegate.columns[0].defaultValueComputed
		assertNull delegate.columns[0].autoIncrement
		assertNull delegate.columns[0].startWith
		assertNull delegate.columns[0].incrementBy
		assertNull delegate.columns[0].remarks
		assertNull delegate.columns[0].defaultValueSequenceNext
		assertNull delegate.columns[0].constraints
	}

	/**
	 * Build a column with no attributes and an empty closure to make sure we
	 * don't introduce any unintended defaults.  The main difference between this
	 * and the no closure version is that the presence of a closure will cause
	 * the column to gain constraints with their defaults.
	 */
	@Test
	void oneColumnEmptyWithClosure() {
		def delegate = buildColumnDelegate(ColumnConfig.class) {
			column([:]) {}
		}

		assertNull delegate.whereClause
		assertEquals 1, delegate.columns.size()
		assertTrue delegate.columns[0] instanceof ColumnConfig
		assertNull delegate.columns[0].name
		assertNull delegate.columns[0].type
		assertNull delegate.columns[0].value
		assertNull delegate.columns[0].valueNumeric
		assertNull delegate.columns[0].valueBoolean
		assertNull delegate.columns[0].valueDate
		assertNull delegate.columns[0].valueComputed
		assertNull delegate.columns[0].valueSequenceNext
		assertNull delegate.columns[0].valueSequenceCurrent
		assertNull delegate.columns[0].valueBlobFile
		assertNull delegate.columns[0].valueClobFile
		assertNull delegate.columns[0].defaultValue
		assertNull delegate.columns[0].defaultValueNumeric
		assertNull delegate.columns[0].defaultValueDate
		assertNull delegate.columns[0].defaultValueBoolean
		assertNull delegate.columns[0].defaultValueComputed
		assertNull delegate.columns[0].autoIncrement
		assertNull delegate.columns[0].startWith
		assertNull delegate.columns[0].incrementBy
		assertNull delegate.columns[0].remarks
		assertNull delegate.columns[0].defaultValueSequenceNext
		assertNotNull delegate.columns[0].constraints
	}

	/**
	 * Test creating a column with all currently supported Liquibase attributes.
	 * There are a lot of them, and not all of them are documented. We'd never
	 * use all of them at the same time, but we're only concerned with making
	 * sure any given attribute is properly passed to Liquibase.  Making sure
	 * a change is valid from a Liquibase point of view is between Liquibase and
	 * the change set author.  Note that care was taken to make sure none of the
	 * attribute values match the attribute names.
	 */
	@Test
	void oneColumnFull() {
		def dateValue = "2010-11-02 07:52:04"
		def columnDateValue = parseSqlTimestamp(dateValue)
		def defaultDate = "2013-12-31 09:30:04"
		def columnDefaultDate = parseSqlTimestamp(defaultDate)

		def delegate = buildColumnDelegate(ColumnConfig.class) {
			column(name: 'columnName',
						 type: 'varchar(30)',
						 value: 'someValue',
						 valueNumeric: 1,
						 valueBoolean: false,
						 valueDate: dateValue,
						 valueComputed: new DatabaseFunction('databaseValue'),
						 valueSequenceNext: new SequenceNextValueFunction('sequenceNext'),
						 valueSequenceCurrent: new SequenceCurrentValueFunction('sequenceCurrent'),
						 valueBlobFile: 'someBlobFile',
						 valueClobFile: 'someClobFile',
						 defaultValue: 'someDefaultValue',
						 defaultValueNumeric: 2,
						 defaultValueDate: defaultDate,
						 defaultValueBoolean: false,
						 defaultValueComputed: new DatabaseFunction("defaultDatabaseValue"),
						 autoIncrement: true, // should be the only true.
						 startWith: 3,
						 incrementBy: 4,
						 remarks: 'No comment',
						 defaultValueSequenceNext: new SequenceNextValueFunction('defaultSequence'))
		}

		assertNull delegate.whereClause
		assertEquals 1, delegate.columns.size()
		assertTrue delegate.columns[0] instanceof ColumnConfig
		assertEquals 'columnName', delegate.columns[0].name
		assertEquals 'varchar(30)', delegate.columns[0].type
		assertEquals 'someValue', delegate.columns[0].value
		assertEquals 1, delegate.columns[0].valueNumeric
		assertFalse delegate.columns[0].valueBoolean
		assertEquals columnDateValue, delegate.columns[0].valueDate
		assertEquals 'databaseValue', delegate.columns[0].valueComputed.value
		assertEquals 'sequenceNext', delegate.columns[0].valueSequenceNext.value
		assertEquals 'sequenceCurrent', delegate.columns[0].valueSequenceCurrent.value
		assertEquals 'someBlobFile', delegate.columns[0].valueBlobFile
		assertEquals 'someClobFile', delegate.columns[0].valueClobFile
		assertEquals 'someDefaultValue', delegate.columns[0].defaultValue
		assertEquals 2, delegate.columns[0].defaultValueNumeric
		assertEquals columnDefaultDate, delegate.columns[0].defaultValueDate
		assertFalse delegate.columns[0].defaultValueBoolean
		assertEquals 'defaultDatabaseValue', delegate.columns[0].defaultValueComputed.value
		assertTrue delegate.columns[0].autoIncrement
		assertEquals 3G, delegate.columns[0].startWith
		assertEquals 4G, delegate.columns[0].incrementBy
		assertEquals 'No comment', delegate.columns[0].remarks
		assertEquals 'defaultSequence', delegate.columns[0].defaultValueSequenceNext.value
		assertNull delegate.columns[0].constraints
	}

	/**
	 * Try adding more than one column.  We don't need full columns, we just want
	 * to make sure we can handle more than one column. This will also let us
	 * isolate the booleans a little better.
	 */
	@Test
	void twoColumns() {
		def delegate = buildColumnDelegate(ColumnConfig.class) {
			// first one has only the boolean value set to true
			column(name: 'first',
						 valueBoolean: true,
			       defaultValueBoolean: false,
			       autoIncrement: false)
			// the second one has just the default value set to true.
			column(name: 'second',
						 valueBoolean: false,
						 defaultValueBoolean: true,
						 autoIncrement: false)
		}

		assertNull delegate.whereClause
		assertEquals 2, delegate.columns.size()
		assertTrue delegate.columns[0] instanceof ColumnConfig
		assertEquals 'first', delegate.columns[0].name
		assertTrue delegate.columns[0].valueBoolean
		assertFalse delegate.columns[0].defaultValueBoolean
		assertFalse delegate.columns[0].autoIncrement
		assertNull delegate.columns[0].constraints
		assertTrue delegate.columns[1] instanceof ColumnConfig
		assertEquals 'second', delegate.columns[1].name
		assertFalse delegate.columns[1].valueBoolean
		assertTrue delegate.columns[1].defaultValueBoolean
		assertFalse delegate.columns[1].autoIncrement
		assertNull delegate.columns[1].constraints

	}

	/**
	 * Try a column that contains a constraint.  We're not concerned with the
	 * contents of the constraint, just that the closure could be called, and the
	 * contents added to the column.
	 */
	@Test
	void columnWithConstraint() {
		def delegate = buildColumnDelegate(ColumnConfig.class) {
			// first one has only the boolean value set to true
			column(name: 'first',
						 type: 'int') {
				constraints(nullable: false, unique: true)
			}
		}

		assertNull delegate.whereClause
		assertEquals 1, delegate.columns.size()
		assertTrue delegate.columns[0] instanceof ColumnConfig
		assertEquals 'first', delegate.columns[0].name
		assertEquals 'int', delegate.columns[0].type
		assertNotNull delegate.columns[0].constraints
		assertFalse delegate.columns[0].constraints.nullable
		assertTrue delegate.columns[0].constraints.unique
	}

	/**
	 * Test creating a "loadData" column with all currently supported Liquibase
	 * attributes. A "loadData" column is the same as a normal column, but adds
	 * 2 new attributes.  Let's repeat the {@link #oneColumnFull()} test, but
	 * change the type of column to create to make sure we can set the 2 new
	 * attributes.  This is the only "loadData" test we'll have since there is
	 * not any code in the Delegate itself that does anything different for
	 * "loadData" columns.  It makes a different type of object because the
	 * caller tells it to.
	 */
	@Test
	void oneLoadDataColumnFull() {
		def dateValue = "2010-11-02 07:52:04"
		def columnDateValue = parseSqlTimestamp(dateValue)
		def defaultDate = "2013-12-31 09:30:04"
		def columnDefaultDate = parseSqlTimestamp(defaultDate)

		def delegate = buildColumnDelegate(LoadDataColumnConfig.class) {
			column(name: 'columnName',
							type: 'varchar(30)',
							value: 'someValue',
							valueNumeric: 1,
							valueBoolean: false,
							valueDate: dateValue,
							valueComputed: new DatabaseFunction('databaseValue'),
							valueSequenceNext: new SequenceNextValueFunction('sequenceNext'),
							valueSequenceCurrent: new SequenceCurrentValueFunction('sequenceCurrent'),
							valueBlobFile: 'someBlobFile',
							valueClobFile: 'someClobFile',
							defaultValue: 'someDefaultValue',
							defaultValueNumeric: 2,
							defaultValueDate: defaultDate,
							defaultValueBoolean: false,
							defaultValueComputed: new DatabaseFunction("defaultDatabaseValue"),
							autoIncrement: true, // should be the only true.
							startWith: 3,
							incrementBy: 4,
							remarks: 'No comment',
							defaultValueSequenceNext: new SequenceNextValueFunction('defaultSequence'),
			        header: 'columnHeader',
			        index: 5)
		}

		assertNull delegate.whereClause
		assertEquals 1, delegate.columns.size()
		assertTrue delegate.columns[0] instanceof LoadDataColumnConfig
		assertEquals 'columnName', delegate.columns[0].name
		assertEquals 'varchar(30)', delegate.columns[0].type
		assertEquals 'someValue', delegate.columns[0].value
		assertEquals 1, delegate.columns[0].valueNumeric
		assertFalse delegate.columns[0].valueBoolean
		assertEquals columnDateValue, delegate.columns[0].valueDate
		assertEquals 'databaseValue', delegate.columns[0].valueComputed.value
		assertEquals 'sequenceNext', delegate.columns[0].valueSequenceNext.value
		assertEquals 'sequenceCurrent', delegate.columns[0].valueSequenceCurrent.value
		assertEquals 'someBlobFile', delegate.columns[0].valueBlobFile
		assertEquals 'someClobFile', delegate.columns[0].valueClobFile
		assertEquals 'someDefaultValue', delegate.columns[0].defaultValue
		assertEquals 2, delegate.columns[0].defaultValueNumeric
		assertEquals columnDefaultDate, delegate.columns[0].defaultValueDate
		assertFalse delegate.columns[0].defaultValueBoolean
		assertEquals 'defaultDatabaseValue', delegate.columns[0].defaultValueComputed.value
		assertTrue delegate.columns[0].autoIncrement
		assertEquals 3G, delegate.columns[0].startWith
		assertEquals 4G, delegate.columns[0].incrementBy
		assertEquals 'No comment', delegate.columns[0].remarks
		assertEquals 'defaultSequence', delegate.columns[0].defaultValueSequenceNext.value
		assertEquals 'columnHeader', delegate.columns[0].header
		assertEquals 5, delegate.columns[0].index
		assertNull delegate.columns[0].constraints
	}

	/**
	 * Test a column closure that has a where clause.
	 */
	@Test
	void columnClosureCanContainWhereClause() {
		def delegate = buildColumnDelegate(ColumnConfig.class) {
			column(name: 'monkey', type: 'VARCHAR(50)')
			where "emotion='angry'"
		}

		assertNotNull delegate.columns
		assertEquals 1, delegate.columns.size()
		assertTrue delegate.columns[0] instanceof ColumnConfig
		assertEquals 'monkey', delegate.columns[0].name
		assertEquals "emotion='angry'", delegate.whereClause
	}

	/**
	 * {@code delete} changes will have a where clause, but no actual columns.
	 * Make sure we can handle this.
	 */
	@Test
	void columnClosureIsJustWhereClause() {
		def delegate = buildColumnDelegate(ColumnConfig.class) {
			where "emotion='angry'"
		}

		assertNotNull delegate.columns
		assertEquals 0, delegate.columns.size()
		assertEquals "emotion='angry'", delegate.whereClause
	}

	/**
	 * Try an invalid method in the closure to make sure we get our
	 * IllegalArgumentException instead of the standard MissingMethodException.
	 */
	@Test(expected = IllegalArgumentException)
	void invalidMethodInClosure() {
		def delegate = buildColumnDelegate(ColumnConfig.class) {
			table(name: 'monkey')
		}

		assertNotNull delegate.columns
		assertEquals 0, delegate.columns.size()
		assertEquals "emotion='angry'", delegate.whereClause
	}


	/**
	 * Try building a column when it contains an invalid attribute.  Do we
	 * get an IllegalArgumentException, which will have our pretty message?
	 * We try to trick the system by using what is a valid "loadData" column
	 * attribute on a normal ColumnConfig.
	 */
	@Test(expected = IllegalArgumentException)
	void columnWithInvalidAttribute() {
		def delegate = buildColumnDelegate(ColumnConfig.class) {
			column(header: 'invalid')
		}
	}


	// Test invalid

	/**
	 * helper method to build and execute a ColumnDelegate.
	 * @param closure the closure to execute
	 * @return the new delegate.
	 */
  def buildColumnDelegate(Class columnConfigClass, Closure closure) {
      def changelog = new DatabaseChangeLog()
      changelog.changeLogParameters = new ChangeLogParameters()
	    def columnDelegate = new ColumnDelegate(columnConfigClass: columnConfigClass,
					                                    databaseChangeLog: changelog,
					                                    changeSetId: 'test-change-set',
					                                    changeName: 'create-table')
      closure.delegate = columnDelegate
      closure.resolveStrategy = Closure.DELEGATE_FIRST
      closure.call()
      
      return columnDelegate
  }

	/**
	 * Helper method to parse a string into a date.
	 * @param dateTimeString the string to parse
	 * @return the parsed string
	 */
  private Timestamp parseSqlTimestamp(dateTimeString) {
    new Timestamp(sdf.parse(dateTimeString).time)
  }
}

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
import liquibase.change.core.LoadDataColumnConfig
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.ChangeSet
import liquibase.changelog.DatabaseChangeLog
import liquibase.exception.ChangeLogParseException
import liquibase.sql.visitor.AppendSqlVisitor
import liquibase.sql.visitor.PrependSqlVisitor
import liquibase.sql.visitor.RegExpReplaceSqlVisitor
import liquibase.sql.visitor.ReplaceSqlVisitor
import liquibase.statement.DatabaseFunction
import liquibase.statement.SequenceCurrentValueFunction
import liquibase.statement.SequenceNextValueFunction
import org.junit.Test

import java.sql.Timestamp
import java.text.SimpleDateFormat

import static org.junit.Assert.*

/**
 * Test class for the {@link ModifySqlDelegate}.  As usual, we're only verifying
 * that we can pass things to Liquibase correctly. We check all attributes that
 * are known at this time - note that several are undocumented.  Thia
 *
 * @author Steven C. Saliman
 */
class ModifySqlDelegateTests {

	/**
	 * Test the modifySql delegate with empty attributes and an empty closure.
	 */
	@Test
	void modifySqlEmpty() {
		def sqlVisitors = buildDelegate([:]) {}
		assertEquals 0, sqlVisitors.size()
	}

	/**
	 * Test modifySql with an unsupported attribute.
	 */
	@Test(expected = ChangeLogParseException)
	void modifySqlInvalidAttribute() {
		buildDelegate(name: 'test') {
			prepend(value: "select * from monkey")
		}
	}

	/**
	 * Test modifySql with an unsupported element in the closure.
	 */
	@Test(expected = ChangeLogParseException)
	void modifySqlInvalidElement() {
		buildDelegate(null) {
			makeUpSql(value: "select * from monkey")
		}
	}

	/**
	 * Test the prepend element with no attributes. Validate the default
	 * "applyToRollback" value of false.
	 */
	@Test
	void prependEmpty() {
		def sqlVisitors = buildDelegate([:]) {
			prepend([:])
		}
		assertEquals 1, sqlVisitors.size()
		assertTrue sqlVisitors[0] instanceof PrependSqlVisitor
    assertNull sqlVisitors[0].value
		assertNull sqlVisitors[0].applicableDbms
		assertNull sqlVisitors[0].contexts
		assertFalse sqlVisitors[0].applyToRollback

	}

	/**
	 * Test a prepend with an invalid attribute.
	 */
	@Test(expected = ChangeLogParseException)
	void prependInvalidAttribute() {
		buildDelegate([:]) {
			prepend(prefix: 'exec')
		}
	}

	/**
	 * Test the prepend element with all supported attributes.  For this test,
	 * we will also set a dbms.
	 */
	@Test
	void prependFull() {
		def sqlVisitors = buildDelegate(dbms: 'mysql') {
			prepend(value: 'exec')
		}
		assertEquals 1, sqlVisitors.size()
		assertTrue sqlVisitors[0] instanceof PrependSqlVisitor
		assertEquals 'exec', sqlVisitors[0].value
		assertEquals 'mysql', sqlVisitors[0].applicableDbms.toArray()[0]
		assertNull sqlVisitors[0].contexts
		assertFalse sqlVisitors[0].applyToRollback
	}

	/**
	 * Test the append element with no attributes. This time, we'll also make sure
	 * we can set applyToRollback to true.
	 */
	@Test
	void appendEmpty() {
		def sqlVisitors = buildDelegate(applyToRollback: true) {
			append([:])
		}
		assertEquals 1, sqlVisitors.size()
		assertTrue sqlVisitors[0] instanceof AppendSqlVisitor
		assertNull sqlVisitors[0].value
		assertNull sqlVisitors[0].applicableDbms
		assertNull sqlVisitors[0].contexts
		assertTrue sqlVisitors[0].applyToRollback

	}

	/**
	 * Test an append with an invalid attribute.
	 */
	@Test(expected = ChangeLogParseException)
	void appendInvalidAttribute() {
		buildDelegate([:]) {
			append(suffix: 'exec')
		}
	}

	/**
	 * Test the append element with all supported attributes.  For this test,
	 * we will also set a context.
	 */
	@Test
	void appendFull() {
		def sqlVisitors = buildDelegate(context: 'test') {
			append(value: 'exec')
		}
		assertEquals 1, sqlVisitors.size()
		assertTrue sqlVisitors[0] instanceof AppendSqlVisitor
		assertEquals 'exec', sqlVisitors[0].value
		assertNull sqlVisitors[0].applicableDbms
		assertEquals 'test', sqlVisitors[0].contexts.toArray()[0]
		assertFalse sqlVisitors[0].applyToRollback
	}

	/**
	 * Test the replace element with no attributes. This time, we'll try to
	 * explicitly set applyToRollback to false.
	 */
	@Test
	void replaceEmpty() {
		def sqlVisitors = buildDelegate(applyToRollback: false) {
			replace([:])
		}
		assertEquals 1, sqlVisitors.size()
		assertTrue sqlVisitors[0] instanceof ReplaceSqlVisitor
		assertNull sqlVisitors[0].replace
		assertNull sqlVisitors[0].with
		assertNull sqlVisitors[0].applicableDbms
		assertNull sqlVisitors[0].contexts
		assertFalse sqlVisitors[0].applyToRollback

	}

	/**
	 * Test a replace with an invalid attribute.
	 */
	@Test(expected = ChangeLogParseException)
	void replaceInvalidAttribute() {
		buildDelegate([:]) {
			replace(delete: 'select')
		}
	}

	/**
	 * Test the replace element with all supported attributes.  For this test,
	 * we will also set a 2 databases to test that it gets split correctly.
	 */
	@Test
	void replaceFull() {
		def sqlVisitors = buildDelegate(dbms: 'mysql,oracle') {
			replace(replace: 'execute', with: 'exec')
		}
		assertEquals 1, sqlVisitors.size()
		assertTrue sqlVisitors[0] instanceof ReplaceSqlVisitor
		assertEquals 'execute', sqlVisitors[0].replace
		assertEquals 'exec', sqlVisitors[0].with
		assertEquals 2, sqlVisitors[0].applicableDbms.size()
		assertTrue sqlVisitors[0].applicableDbms.contains('mysql')
		assertTrue sqlVisitors[0].applicableDbms.contains('oracle')
		assertNull sqlVisitors[0].contexts
		assertFalse sqlVisitors[0].applyToRollback
	}

	/**
	 * Test the regExpReplace element with no attributes. This time, we'll also make sure
	 * we can set applyToRollback to the string "true".
	 */
	@Test
	void regExpReplaceEmpty() {
		def sqlVisitors = buildDelegate(applyToRollback: 'true') {
			regExpReplace([:])
		}
		assertEquals 1, sqlVisitors.size()
		assertTrue sqlVisitors[0] instanceof RegExpReplaceSqlVisitor
		assertNull sqlVisitors[0].replace
		assertNull sqlVisitors[0].with
		assertNull sqlVisitors[0].applicableDbms
		assertNull sqlVisitors[0].contexts
		assertTrue sqlVisitors[0].applyToRollback

	}

	/**
	 * Test a regExpReplace with an invalid attribute.
	 */
	@Test(expected = ChangeLogParseException)
	void regExpReplaceInvalid() {
		buildDelegate([:]) {
			regExpReplace(delete: 'exec')
		}
	}

	/**
	 * Test the regExpReplace element with all supported attributes.  For this test,
	 * we will also set two contexts.
	 */
	// replaceRegex full use 2 contexts
	@Test
	void regExpReplaceFull() {
		def sqlVisitors = buildDelegate(context: 'test,ci') {
			regExpReplace(replace: 'execute', with: 'exec')
		}
		assertEquals 1, sqlVisitors.size()
		assertTrue sqlVisitors[0] instanceof RegExpReplaceSqlVisitor
		assertEquals 'execute', sqlVisitors[0].replace
		assertEquals 'exec', sqlVisitors[0].with
		assertNull sqlVisitors[0].applicableDbms
		assertEquals 2, sqlVisitors[0].contexts.size()
		assertTrue sqlVisitors[0].contexts.contains('test')
		assertTrue sqlVisitors[0].contexts.contains('ci')
		assertFalse sqlVisitors[0].applyToRollback
	}

	/**
	 * helper method to build and execute a ModifySqlDelegate.
	 * @param params the parameters to pass to the new delegate
	 * @param closure the closure to execute
	 * @return the new delegate.
	 */
	def buildDelegate(Map params, Closure closure) {
		def changeLog = new DatabaseChangeLog()
		changeLog.changeLogParameters = new ChangeLogParameters()
		def changeSet = new ChangeSet(
							'test-change',
							'stevesaliman',
							false,
							false,
							'/file',
							'context',
							'mysql',
							true,
						  changeLog)


		def delegate = new ModifySqlDelegate(params, changeSet)
		closure.delegate = delegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()

		return delegate.sqlVisitors
	}

}

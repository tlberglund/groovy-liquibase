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

import liquibase.exception.ChangeLogParseException
import liquibase.precondition.core.NotPrecondition;
import org.junit.Test
import static org.junit.Assert.*
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.DatabaseChangeLog
import liquibase.precondition.core.DBMSPrecondition
import liquibase.precondition.Precondition
import liquibase.precondition.core.RunningAsPrecondition
import liquibase.precondition.core.ChangeSetExecutedPrecondition
import liquibase.precondition.core.ColumnExistsPrecondition
import liquibase.precondition.core.TableExistsPrecondition
import liquibase.precondition.core.ViewExistsPrecondition
import liquibase.precondition.core.ForeignKeyExistsPrecondition
import liquibase.precondition.core.IndexExistsPrecondition
import liquibase.precondition.core.SequenceExistsPrecondition
import liquibase.precondition.core.PrimaryKeyExistsPrecondition
import liquibase.precondition.core.AndPrecondition
import liquibase.precondition.core.OrPrecondition
import liquibase.precondition.core.SqlPrecondition
import liquibase.precondition.CustomPreconditionWrapper

/**
 * This class tests the creation of Liquibase ChangeSet Preconditions.  It is
 * probably a bit of overkill since most preconditions are set by passing
 * named preconditions to the Liquibase factory, but it does serve to make sure
 * that ll the preconditions currently known will work as we would expect.
 * <p>
 * since we just pass through to Liquibase, we're not too concerned with
 * validating attrubutes.
 *
 * @author Tim Berglund
 * @author Steven C. Saliman
 */
class PreconditionDelegateTests {

	/**
	 * Try creating a dbms precondition
	 */
	@Test
	void dbmsPrecondition() {
		def preconditions = buildPreconditions {
			dbms(type: 'mysql')
		}

		assertNotNull preconditions
		assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof DBMSPrecondition
		assertEquals 'mysql', preconditions[0].type
	}

	/**
	 * Try creating a runningAs precondition.
	 */
	@Test
	void runningAsPrecondition() {
		def preconditions = buildPreconditions {
			runningAs(username: 'tlberglund')
		}

		assertNotNull preconditions
		assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof RunningAsPrecondition
		assertEquals 'tlberglund', preconditions[0].username
	}

	/**
	 * Try creating a changeSetExecuted precondition.
	 */
	@Test
	void changeSetExecutedPrecondition() {
		def preconditions = buildPreconditions {
			changeSetExecuted(id: 'unleash-monkey', author: 'tlberglund', changeLogFile: 'changelog.xml')
		}

		assertNotNull preconditions
		assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof ChangeSetExecutedPrecondition
		assertEquals 'unleash-monkey', preconditions[0].id
		assertEquals 'tlberglund', preconditions[0].author
		assertEquals 'changelog.xml', preconditions[0].changeLogFile
	}

	/**
	 * Try creating a columnExists precondition.
	 */
	@Test
	void columnExistsPrecondition() {
		def preconditions = buildPreconditions {
			columnExists(schemaName: 'schema', tableName: 'monkey', columnName: 'emotion')
		}

		assertNotNull preconditions
		assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof ColumnExistsPrecondition
		assertEquals 'schema', preconditions[0].schemaName
		assertEquals 'monkey', preconditions[0].tableName
		assertEquals 'emotion', preconditions[0].columnName
	}

	/**
	 * try creating a tableExists precondition.
	 */
	@Test
	void tableExistsPrecondition() {
		def preconditions = buildPreconditions {
			tableExists(schemaName: 'schema', tableName: 'monkey')
		}

		assertNotNull preconditions
		assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof TableExistsPrecondition
		assertEquals 'schema', preconditions[0].schemaName
		assertEquals 'monkey', preconditions[0].tableName
	}

	/**
	 * Try creating a vewExists precondition.
	 */
	@Test
	void viewExistsPrecondition() {
		def preconditions = buildPreconditions {
			viewExists(schemaName: 'schema', viewName: 'monkey_view')
		}

		assertNotNull preconditions
		assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof ViewExistsPrecondition
		assertEquals 'schema', preconditions[0].schemaName
		assertEquals 'monkey_view', preconditions[0].viewName
	}

	/**
	 * Try creating a foreignKeyConstraintExists precondition
	 */
	@Test
	void foreignKeyConstraintExistsPrecondition() {
		def preconditions = buildPreconditions {
			foreignKeyConstraintExists(schemaName: 'schema', foreignKeyName: 'fk_monkey_key')
		}

		assertNotNull preconditions
		assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof ForeignKeyExistsPrecondition
		assertEquals 'schema', preconditions[0].schemaName
		assertEquals 'fk_monkey_key', preconditions[0].foreignKeyName
	}

	/**
	 * Try creating an indexExists precondition.
	 */
	@Test
	void indexExistsPrecondition() {
		def preconditions = buildPreconditions {
			indexExists(schemaName: 'schema', indexName: 'index')
		}

		assertNotNull preconditions
		assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof IndexExistsPrecondition
		assertEquals 'schema', preconditions[0].schemaName
		assertEquals 'index', preconditions[0].indexName
	}

	/**
	 * Try creating a sequenceExists precondition.
	 */
	@Test
	void sequenceExistsPrecondition() {
		def preconditions = buildPreconditions {
			sequenceExists(schemaName: 'schema', sequenceName: 'seq_next_monkey')
		}

		assertNotNull preconditions
		assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof SequenceExistsPrecondition
		assertEquals 'schema', preconditions[0].schemaName
		assertEquals 'seq_next_monkey', preconditions[0].sequenceName
	}

	/**
	 * Try creating a primaryKeyExists precondition.
	 */
	@Test
	void primaryKeyExistsPrecondition() {
		def preconditions = buildPreconditions {
			primaryKeyExists(schemaName: 'schema', primaryKeyName: 'pk_monkey')
		}

		assertNotNull preconditions
		assertTrue preconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof PrimaryKeyExistsPrecondition
		assertEquals 'schema', preconditions[0].schemaName
		assertEquals 'pk_monkey', preconditions[0].primaryKeyName
	}

	/**
	 * And clauses are handled a little differently. Make sure we can create it
	 * correctly.
	 */
	@Test
	void andClause() {
		def preconditions = buildPreconditions {
			and {
				dbms(type: 'mysql')
				runningAs(username: 'tlberglund')
			}
		}

		assertNotNull preconditions
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof AndPrecondition
		def andedPreconditions = preconditions[0].nestedPreconditions
		assertNotNull andedPreconditions
		assertEquals 2, andedPreconditions.size()
		assertTrue andedPreconditions[0] instanceof DBMSPrecondition
		assertTrue andedPreconditions[1] instanceof RunningAsPrecondition
	}

	/**
	 * Or clauses are handled a little differently. Make sure we can create it
	 * correctly.
	 */
	@Test
	void orClause() {
		def preconditions = buildPreconditions {
			or {
				dbms(type: 'mysql')
				runningAs(username: 'tlberglund')
			}
		}

		assertNotNull preconditions
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof OrPrecondition
		def oredPreconditions = preconditions[0].nestedPreconditions
		assertNotNull oredPreconditions
		assertEquals 2, oredPreconditions.size()
		assertTrue oredPreconditions[0] instanceof DBMSPrecondition
		assertTrue oredPreconditions[1] instanceof RunningAsPrecondition
	}

	/**
	 * Not clauses are handled a little differently. Make sure we can create it
	 * correctly.
	 */
	@Test
	void notClause() {
		def preconditions = buildPreconditions {
			not {
				dbms(type: 'mysql')
				runningAs(username: 'tlberglund')
			}
		}

		assertNotNull preconditions
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof NotPrecondition
		def notedPreconditions = preconditions[0].nestedPreconditions
		assertNotNull notedPreconditions
		assertEquals 2, notedPreconditions.size()
		assertTrue notedPreconditions[0] instanceof DBMSPrecondition
		assertTrue notedPreconditions[1] instanceof RunningAsPrecondition
	}

	// sqlCheck empty
	/**
	 * SqlCheck preconditions are treated a little different than most. Try
	 * creating one with no attributes and an empty closure to make sure we
	 * get no side effects.
	 */
	@Test
	void sqlCheckEmpty() {
		def preconditions = buildPreconditions {
			sqlCheck([:]) {	}
		}

		assertNotNull preconditions
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof SqlPrecondition
		assertNull preconditions[0].expectedResult
		assertNull preconditions[0].sql
	}

	/**
	 * Try creating a sqlCheck precondition with an invalid attribute
	 */
	@Test(expected = ChangeLogParseException)
	void sqlCheckInvalidAttribute() {
		buildPreconditions {
			sqlCheck(expected: 'angry') {
				"SELECT emotion FROM monkey WHERE id=2884"
			}
		}
	}

	/**
	 * Try creating a sqlCheck precondition with all currently known attributes
	 * and some SQL in the closure.
	 */
	@Test
	void sqlCheckFull() {
		def preconditions = buildPreconditions {
			sqlCheck(expectedResult: 'angry') {
				"SELECT emotion FROM monkey WHERE id=2884"
			}
		}

		assertNotNull preconditions
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof SqlPrecondition
		assertEquals 'angry', preconditions[0].expectedResult
		assertEquals 'SELECT emotion FROM monkey WHERE id=2884', preconditions[0].sql
	}

	/**
	 * customPrecondition preconditions are also handled a little differently, so
	 * we need some more checks here. This first test sees what happens when a
	 * custom precondition is made with an invalid attribute.
	 */
	@Test(expected = ChangeLogParseException)
	void customPreconditionInvalidAttribute() {
		buildPreconditions {
			customPrecondition(className: 'org.liquibase.precondition.MonkeyFailPrecondition') {
				param(paramName: 'emotion', value: 'angry')
			}
		}
	}

	/**
	 * Try a custom precondition with a nested param that has an invalid attribute
	 */
	@Test(expected = ChangeLogParseException)
	void customPreconditionInvalidParamAttribute() {
		buildPreconditions {
			customPrecondition(className: 'org.liquibase.precondition.MonkeyFailPrecondition') {
				param(paramName: 'emotion')
			}
		}
	}

	/**
	 * Try a custom precondition with a nested param that has an invalid attribute
	 */
	@Test(expected = ChangeLogParseException)
	void customPreconditionMissingName() {
		buildPreconditions {
			customPrecondition(className: 'org.liquibase.precondition.MonkeyFailPrecondition') {
				param(value: 'angry')
			}
		}
	}

	/**
	 * Test creating a custom precondition with a parameter that has a name but
	 * no value.  It is unusual, but legal.  When this happens, the missing
	 * value will be converted by Liquibase to the word "null"
	 */
	@Test
	void customPreconditionMissingValue() {
		def preconditions = buildPreconditions {
			customPrecondition(className: 'org.liquibase.precondition.MonkeyFailPrecondition') {
				param(name: 'emotion')
			}
		}

		assertNotNull preconditions
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof CustomPreconditionWrapper
		def params = preconditions[0].paramValues
		assertEquals 1, preconditions[0].paramValues.size()
		assertNull params.emotion
	}

	/**
	 * Try creating a custom precondition with 2 nested param elements.
	 */
	@Test
	void customPreconditionTwoParamElements() {
		def preconditions = buildPreconditions {
			customPrecondition(className: 'org.liquibase.precondition.MonkeyFailPrecondition') {
				param(name: 'emotion', value: 'angry')
				param(name: 'rfid-tag', value: 28763)
			}
		}

		assertNotNull preconditions
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof CustomPreconditionWrapper
		def params = preconditions[0].paramValues
		assertEquals 2, preconditions[0].paramValues.size()
		assertEquals 'angry', params.emotion
		assertEquals '28763', params['rfid-tag'] // Liquibase converts to string.
	}

	/**
	 * Test creating a precondition using nested methods instead of 'param'
	 * elements.
	 */
	@Test
	void customPreconditionFails() {
		def preconditions = buildPreconditions {
			customPrecondition(className: 'org.liquibase.precondition.MonkeyFailPrecondition') {
				emotion('angry')
				'rfid-tag'(28763)
			}
		}

		assertNotNull preconditions
		assertEquals 1, preconditions.size()
		assertTrue preconditions[0] instanceof CustomPreconditionWrapper
		def params = preconditions[0].paramValues
		assertEquals 2, preconditions[0].paramValues.size()
		assertEquals 'angry', params.emotion
		assertEquals '28763', params['rfid-tag'] // Liquibase converts to string.
	}

	/**
	 * Try creating an invalid precondition
	 */
	@Test(expected = ChangeLogParseException)
	void invalidPrecondition() {
		buildPreconditions {
			linkExists(host: 'www.thewebsiteisdown.com')
		}
	}

	/**
	 * Try creating a valid precondition, but with an invalid attribute.
	 */
	@Test(expected = ChangeLogParseException)
	void invalidPreconditionAttribute() {
		buildPreconditions {
			tableExists(name: 'monkey') // this is the wrong attribute on purpose
		}
	}

	/**
	 * Helper method to run the precondition closure and return the preconditions.
	 * @param closure the closure to call
	 * @return the preconditions that were created.
	 */
	private def buildPreconditions(Closure closure) {
		def changelog = new DatabaseChangeLog()
		changelog.changeLogParameters = new ChangeLogParameters()

		def delegate = new PreconditionDelegate(databaseChangeLog: changelog)
		closure.delegate = delegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()

		delegate.preconditions
	}
}
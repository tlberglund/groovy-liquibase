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
import liquibase.change.core.CreateIndexChange
import liquibase.change.core.DropIndexChange
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.DatabaseChangeLog
import liquibase.exception.ChangeLogParseException
import liquibase.parser.ChangeLogParser
import liquibase.parser.ChangeLogParserFactory
import liquibase.parser.ext.GroovyLiquibaseChangeLogParser
import liquibase.precondition.Precondition
import liquibase.precondition.core.DBMSPrecondition
import liquibase.precondition.core.PreconditionContainer
import liquibase.precondition.core.RunningAsPrecondition
import liquibase.resource.FileSystemResourceAccessor
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.lang.reflect.Field

import static org.junit.Assert.*

/**
 * Test class for the {@link DatabaseChangeLogDelegate}
 *
 * @author Tim Berglund
 * @author Steven C. Saliman
 */
class DatabaseChangeLogDelegateTests {
	static final def FILE_PATH = "src/test/changelog"
	static final def TMP_CHANGELOG_DIR = new File("${FILE_PATH}/tmp")
	static final def TMP_INCLUDE_DIR = new File("${TMP_CHANGELOG_DIR}/include")
	static final def EMPTY_CHANGELOG = "${FILE_PATH}/empty-changelog.groovy"
	static final def SIMPLE_CHANGELOG = "${FILE_PATH}/simple-changelog.groovy"

	def resourceAccessor
	ChangeLogParserFactory parserFactory


	@Before
	void registerParser() {
		resourceAccessor = new FileSystemResourceAccessor(baseDirectory: '.')
		parserFactory = ChangeLogParserFactory.instance
		ChangeLogParserFactory.getInstance().register(new GroovyLiquibaseChangeLogParser())
		// make sure we start with clean temporary directories before each test
		TMP_CHANGELOG_DIR.deleteDir()
		TMP_INCLUDE_DIR.mkdirs()
	}

	/**
	 * Attempt to clean up included files and directories.  We do this every time
	 * to make sure we start clean each time.  The includeAll test depends on it.
	 */
	@After
	void cleanUp() {
		TMP_CHANGELOG_DIR.deleteDir()
	}

	@Test
	void parseEmptyChangelog() {
		def parser = parserFactory.getParser(EMPTY_CHANGELOG, resourceAccessor)

		assertNotNull "Groovy changelog parser was not found", parser

		def changeLog = parser.parse(EMPTY_CHANGELOG, null, resourceAccessor)
		assertNotNull "Parsed DatabaseChangeLog was null", changeLog
		assertTrue "Parser result was not a DatabaseChangeLog", changeLog instanceof DatabaseChangeLog
	}


	@Test
	void parseSimpleChangelog() {
		def parser = parserFactory.getParser(SIMPLE_CHANGELOG, resourceAccessor)

		assertNotNull "Groovy changelog parser was not found", parser

		def changeLog = parser.parse(SIMPLE_CHANGELOG, null, resourceAccessor)
		assertNotNull "Parsed DatabaseChangeLog was null", changeLog
		assertTrue "Parser result was not a DatabaseChangeLog", changeLog instanceof DatabaseChangeLog
		assertEquals '.', changeLog.logicalFilePath

		def changeSets = changeLog.changeSets
		assertEquals 1, changeSets.size()
		def changeSet = changeSets[0]
		assertNotNull "ChangeSet was null", changeSet
		assertEquals 'tlberglund', changeSet.author
		assertEquals 'change-set-001', changeSet.id
	}


	@Test(expected=ChangeLogParseException)
	void parsingEmptyDatabaseChangeLogFails() {
		def changeLogFile = createFileFrom(TMP_CHANGELOG_DIR, '.groovy', """
databaseChangeLog()
""")
		def parser = parserFactory.getParser(changeLogFile.absolutePath, resourceAccessor)
		def changeLog = parser.parse(changeLogFile.absolutePath, null, resourceAccessor)
	}


	@Test
	void parsingDatabaseChangeLogAsProperty() {
		File changeLogFile = createFileFrom(TMP_CHANGELOG_DIR, '.groovy', """
    databaseChangeLog = {
    }
    """)
		ChangeLogParser parser = parserFactory.getParser(changeLogFile.absolutePath, resourceAccessor)
		DatabaseChangeLog changeLog = parser.parse(changeLogFile.absolutePath, null, resourceAccessor)

		assertNotNull "Parsed DatabaseChangeLog was null", changeLog
	}


	@Test
	void preconditionParameters() {
		def closure = {
			preConditions(onFail: 'WARN', onError: 'MARK_RAN', onUpdateSQL: 'TEST', onFailMessage: 'fail-message!!!1!!1one!', onErrorMessage: 'error-message') {

			}
		}

		def databaseChangeLog = new DatabaseChangeLog('changelog.xml')
	  databaseChangeLog.changeLogParameters = new ChangeLogParameters()
		def delegate = new DatabaseChangeLogDelegate(databaseChangeLog)
		closure.delegate = delegate
		closure.call()

		def preconditions = databaseChangeLog.preconditions
		assertNotNull preconditions
		assertTrue preconditions instanceof PreconditionContainer
		assertEquals PreconditionContainer.FailOption.WARN, preconditions.onFail
		assertEquals PreconditionContainer.ErrorOption.MARK_RAN, preconditions.onError
		assertEquals PreconditionContainer.OnSqlOutputOption.TEST, preconditions.onSqlOutput
		assertEquals 'fail-message!!!1!!1one!', preconditions.onFailMessage
		assertEquals 'error-message', preconditions.onErrorMessage
	}


	/**
	 * Test creating a changeSet with no attributes. This verifies that we use
	 * expected default values when a value is not provided.
	 */
	@Test
	void changeSetEmpty() {
		def changeLog = buildChangeLog {
			changeSet([:]) {}
		}
		assertNotNull changeLog.changeSets
		assertEquals 1, changeLog.changeSets.size()
		assertNull changeLog.changeSets[0].id
		assertNull changeLog.changeSets[0].author
		assertFalse changeLog.changeSets[0].alwaysRun // the property doesn't match xml or docs.
		assertFalse changeLog.changeSets[0].runOnChange
		assertEquals FILE_PATH, changeLog.changeSets[0].filePath
		assertNull changeLog.changeSets[0].contexts
		assertNull changeLog.changeSets[0].dbmsSet
		assertTrue changeLog.changeSets[0].runInTransaction
	  assertNull changeLog.changeSets[0].failOnError
	  assertEquals "HALT", changeLog.changeSets[0].onValidationFail.toString()
	}

	/**
	 * Test creating a changeSet with all supported attributes.
	 */
	@Test
	void changeSetFull() {
		def changeLog = buildChangeLog {
			changeSet(id: 'monkey-change',
					      author: 'stevesaliman',
							  dbms: 'mysql',
							  runAlways: true,
							  runOnChange: true,
							  context: 'testing',
							  runInTransaction: false,
							  failOnError: true,
							  onValidationFail: "MARK_RAN") {
			  dropTable(tableName: 'monkey')
			}
		}

		assertNotNull changeLog.changeSets
		assertEquals 1, changeLog.changeSets.size()
		assertEquals 'monkey-change', changeLog.changeSets[0].id
		assertEquals 'stevesaliman', changeLog.changeSets[0].author
		assertTrue changeLog.changeSets[0].alwaysRun // the property doesn't match xml or docs.
		assertTrue changeLog.changeSets[0].runOnChange
		assertEquals FILE_PATH, changeLog.changeSets[0].filePath
		assertEquals 'testing', changeLog.changeSets[0].contexts.toArray()[0]
		assertEquals 'mysql', changeLog.changeSets[0].dbmsSet.toArray()[0]
		assertFalse changeLog.changeSets[0].runInTransaction
		assertTrue changeLog.changeSets[0].failOnError
		assertEquals "MARK_RAN", changeLog.changeSets[0].onValidationFail.toString()
	}

	/**
	 * Test creating a changeSet with all attributes, but this time, use the
	 * deprecated "alwaysRun" property.  We'll also set all booleans to the
	 * opposite of the last test.
	 */
	@Test
	void changeSetAlwaysRun() {
		def changeLog = buildChangeLog {
			changeSet(id: 'monkey-change',
							author: 'stevesaliman',
							dbms: 'mysql',
							alwaysRun: true,
							runOnChange: false,
							context: 'testing',
							runInTransaction: true,
							failOnError: false,
							onValidationFail: "MARK_RAN") {
				dropTable(tableName: 'monkey')
			}
		}

		assertNotNull changeLog.changeSets
		assertEquals 1, changeLog.changeSets.size()
		assertEquals 'monkey-change', changeLog.changeSets[0].id
		assertEquals 'stevesaliman', changeLog.changeSets[0].author
		assertTrue changeLog.changeSets[0].alwaysRun // the property doesn't match xml or docs.
		assertFalse changeLog.changeSets[0].runOnChange
		assertEquals FILE_PATH, changeLog.changeSets[0].filePath
		assertEquals 'testing', changeLog.changeSets[0].contexts.toArray()[0]
		assertEquals 'mysql', changeLog.changeSets[0].dbmsSet.toArray()[0]
		assertTrue changeLog.changeSets[0].runInTransaction
		assertFalse changeLog.changeSets[0].failOnError
		assertEquals "MARK_RAN", changeLog.changeSets[0].onValidationFail.toString()
	}

	/**
	 * Test creating a changeSet with an unsupported attribute.
	 */
	@Test(expected = ChangeLogParseException)
	void changeSetInvalidAttribute() {
		buildChangeLog {
			changeSet(id: 'monkey-change',
							author: 'stevesaliman',
							dbms: 'mysql',
							runAlways: false,
							runOnChange: true,
							context: 'testing',
							runInTransaction: false,
							failOnError: true,
							onValidationFail: "MARK_RAN",
			        invalidAttribute: 'invalid') {
				dropTable(tableName: 'monkey')
			}
		}
	}

	/**
	 * Test change log preconditions.  This uses the same delegate as change set
	 * preconditions, so we don't have to do much here, just make sure we can
	 * call the correct thing from a change log and have the change log altered.
	 */
	@Test
	void preconditionsInChangeLog() {
		def changeLog = buildChangeLog {
			preConditions {
				dbms(type: 'mysql')
			}
		}

		assertEquals 0, changeLog.changeSets.size()
		assertNotNull changeLog.preconditions
		assertTrue changeLog.preconditions.nestedPreconditions.every { precondition -> precondition instanceof Precondition }
		assertEquals 1, changeLog.preconditions.nestedPreconditions.size()
		assertTrue changeLog.preconditions.nestedPreconditions[0] instanceof DBMSPrecondition
		assertEquals 'mysql', changeLog.preconditions.nestedPreconditions[0].type

	}

	/**
	 * Test including a file when we have an unsupported attribute.
	 */
	@Test(expected = ChangeLogParseException)
	void includeInvalidAttribute() {
		buildChangeLog {
			include(changeFile: 'invalid')
		}
	}

	/**
	 * Try including a file.
	 */
	@Test
	void includeValid() {
		def includedChangeLogFile = createFileFrom(TMP_INCLUDE_DIR, '.groovy', """
databaseChangeLog {
  preConditions {
    runningAs(username: 'tlberglund')
  }

  changeSet(author: 'tlberglund', id: 'included-change-set') {
    renameTable(oldTableName: 'prosaic_table_name', newTableName: 'monkey')
  }
}
""")

		includedChangeLogFile = includedChangeLogFile.canonicalPath
		includedChangeLogFile = includedChangeLogFile.replaceAll("\\\\", "/")

		def rootChangeLogFile = createFileFrom(TMP_CHANGELOG_DIR, '.groovy', """
databaseChangeLog {
  preConditions {
    dbms(type: 'mysql')
  }
  include(file: '${includedChangeLogFile}')
  changeSet(author: 'tlberglund', id: 'root-change-set') {
    addColumn(tableName: 'monkey') {
      column(name: 'emotion', type: 'varchar(50)')
    }
  }
}
""")

		def parser = parserFactory.getParser(rootChangeLogFile.absolutePath, resourceAccessor)
		def rootChangeLog = parser.parse(rootChangeLogFile.absolutePath, new ChangeLogParameters(), resourceAccessor)

		assertNotNull rootChangeLog
		def changeSets = rootChangeLog.changeSets
		assertNotNull changeSets
		assertEquals 2, changeSets.size()
		assertEquals 'included-change-set', changeSets[0].id
		assertEquals 'root-change-set', changeSets[1].id

		def preconditions = rootChangeLog.preconditionContainer?.nestedPreconditions
		assertNotNull preconditions
		assertEquals 2, preconditions.size()
		assertTrue preconditions[0] instanceof DBMSPrecondition
		assertTrue preconditions[1] instanceof RunningAsPrecondition
	}

	/**
	 * Try including a file relative to the changelolg file.
	 */
	@Test
	void includeRelative() {
		def includedChangeLogFile = createFileFrom(TMP_INCLUDE_DIR, '.groovy', """
databaseChangeLog {
  preConditions {
    runningAs(username: 'tlberglund')
  }

  changeSet(author: 'tlberglund', id: 'included-change-set') {
    renameTable(oldTableName: 'prosaic_table_name', newTableName: 'monkey')
  }
}
""")

		includedChangeLogFile = includedChangeLogFile.name

		def rootChangeLogFile = createFileFrom(TMP_CHANGELOG_DIR, '.groovy', """
databaseChangeLog {
  preConditions {
    dbms(type: 'mysql')
  }
  include(file: 'include/${includedChangeLogFile}', relativeToChangelogFile: true)
  changeSet(author: 'tlberglund', id: 'root-change-set') {
    addColumn(tableName: 'monkey') {
      column(name: 'emotion', type: 'varchar(50)')
    }
  }
}
""")

		def parser = parserFactory.getParser(rootChangeLogFile.absolutePath, resourceAccessor)
		def rootChangeLog = parser.parse(rootChangeLogFile.absolutePath, new ChangeLogParameters(), resourceAccessor)

		assertNotNull rootChangeLog
		def changeSets = rootChangeLog.changeSets
		assertNotNull changeSets
		assertEquals 2, changeSets.size()
		assertEquals 'included-change-set', changeSets[0].id
		assertEquals 'root-change-set', changeSets[1].id

		def preconditions = rootChangeLog.preconditionContainer?.nestedPreconditions
		assertNotNull preconditions
		assertEquals 2, preconditions.size()
		assertTrue preconditions[0] instanceof DBMSPrecondition
		assertTrue preconditions[1] instanceof RunningAsPrecondition
	}

	/**
	 * Test including a path when we have an unsupported attribute.
	 */
	@Test(expected = ChangeLogParseException)
	void includeAllInvalidAttribute() {
		buildChangeLog {
			includeAll(changePath: 'invalid')
		}
	}

	/**
	 * Try including all files in a directory.  For this test, we want 2 files
	 * to make sure we include them both, and in the right order.  Note: when
	 * other tests throw exceptions, this test may also fail because of unclean
	 * directories.  Fix the other tests first.
	 */
	@Test
	void includeAllValid() {
		// The two included change logs need to be created with prefixes that
		// guarantee that the first file will be alphabetically first.
		def includedChangeLogFile = createFileFrom(TMP_INCLUDE_DIR, 'first', '.groovy', """
databaseChangeLog {
  preConditions {
    runningAs(username: 'tlberglund')
  }

  changeSet(author: 'tlberglund', id: 'included-change-set-1') {
    renameTable(oldTableName: 'prosaic_table_name', newTableName: 'monkey')
  }
}
""")

		includedChangeLogFile = createFileFrom(TMP_INCLUDE_DIR, 'second', '-2.groovy', """
databaseChangeLog {
  changeSet(author: 'tlberglund', id: 'included-change-set-2') {
    addColumn(tableName: 'monkey') {
      column(name: 'emotion', type: 'varchar(30)')
    }
  }
}
""")

		includedChangeLogFile = includedChangeLogFile.parentFile.canonicalPath
		includedChangeLogFile = includedChangeLogFile.replaceAll("\\\\", "/")

		def rootChangeLogFile = createFileFrom(TMP_CHANGELOG_DIR, '.groovy', """
databaseChangeLog {
  preConditions {
    dbms(type: 'mysql')
  }
  includeAll(path: '${includedChangeLogFile}')
  changeSet(author: 'tlberglund', id: 'root-change-set') {
    addColumn(tableName: 'monkey') {
      column(name: 'emotion', type: 'varchar(50)')
    }
  }
}
""")

		def parser = parserFactory.getParser(rootChangeLogFile.absolutePath, resourceAccessor)
		def rootChangeLog = parser.parse(rootChangeLogFile.absolutePath, new ChangeLogParameters(), resourceAccessor)

		assertNotNull rootChangeLog
		def changeSets = rootChangeLog.changeSets
		assertNotNull changeSets
		assertEquals 3, changeSets.size()
		assertEquals 'included-change-set-1', changeSets[0].id
		assertEquals 'included-change-set-2', changeSets[1].id
		assertEquals 'root-change-set', changeSets[2].id

		def preconditions = rootChangeLog.preconditionContainer?.nestedPreconditions
		assertNotNull preconditions
		assertEquals 2, preconditions.size()
		assertTrue preconditions[0] instanceof DBMSPrecondition
		assertTrue preconditions[1] instanceof RunningAsPrecondition
	}

	/**
	 * Try including all files in a directory relative to the changelog.
	 */
	@Test
	void includeAllRelative() {
		def includedChangeLogFile = createFileFrom(TMP_INCLUDE_DIR, '.groovy', """
databaseChangeLog {
  preConditions {
    runningAs(username: 'tlberglund')
  }

  changeSet(author: 'tlberglund', id: 'included-change-set') {
    renameTable(oldTableName: 'prosaic_table_name', newTableName: 'monkey')
  }
}
""")

		def rootChangeLogFile = createFileFrom(TMP_CHANGELOG_DIR, '.groovy', """
databaseChangeLog {
  preConditions {
    dbms(type: 'mysql')
  }
  includeAll(path: 'include', relativeToChangelogFile: true)
  changeSet(author: 'tlberglund', id: 'root-change-set') {
    addColumn(tableName: 'monkey') {
      column(name: 'emotion', type: 'varchar(50)')
    }
  }
}
""")

		def parser = parserFactory.getParser(rootChangeLogFile.absolutePath, resourceAccessor)
		def rootChangeLog = parser.parse(rootChangeLogFile.absolutePath, new ChangeLogParameters(), resourceAccessor)

		assertNotNull rootChangeLog
		def changeSets = rootChangeLog.changeSets
		assertNotNull changeSets
		assertEquals 2, changeSets.size()
		assertEquals 'included-change-set', changeSets[0].id
		assertEquals 'root-change-set', changeSets[1].id

		def preconditions = rootChangeLog.preconditionContainer?.nestedPreconditions
		assertNotNull preconditions
		assertEquals 2, preconditions.size()
		assertTrue preconditions[0] instanceof DBMSPrecondition
		assertTrue preconditions[1] instanceof RunningAsPrecondition
	}

	/**
	 * Try adding a property with an invalid attribute
	 */
	@Test(expected = ChangeLogParseException)
	void propertyInvalidAttribute() {
		buildChangeLog {
			property(propertyName: 'invalid', propertyValue: 'invalid')
		}
	}

	/**
	 * Try creating an empty property.
	 */
	@Test
	void propertyEmpty() {
		def changeLog = buildChangeLog {
			property([:])
		}

		// change log parameters are not exposed through the API, so get them
		// using reflection.  Also, there are
		def changeLogParameters = changeLog.changeLogParameters
		Field f = changeLogParameters.getClass().getDeclaredField("changeLogParameters")
		f.setAccessible(true)
		def properties = f.get(changeLogParameters)
		def property = properties[properties.size()-1] // The last one is ours.
		assertNull property.key
		assertNull property.value
		assertNull property.validDatabases
		assertNull property.validContexts
	}

	/**
	 * Try creating a property with a name and value only.  Make sure we don't
	 * try to set the database or contexts
	 */
	@Test
	void propertyPartial() {
		def changeLog = buildChangeLog {
			property(name: 'emotion', value: 'angry')
		}

		// change log parameters are not exposed through the API, so get them
		// using reflection.  Also, there are
		def changeLogParameters = changeLog.changeLogParameters
		Field f = changeLogParameters.getClass().getDeclaredField("changeLogParameters")
		f.setAccessible(true)
		def properties = f.get(changeLogParameters)
		def property = properties[properties.size()-1] // The last one is ours.
		assertEquals 'emotion', property.key
		assertEquals 'angry', property.value
		assertNull property.validDatabases
		assertNull property.validContexts
	}

	/**
	 * Try creating a property with all supported attributes.
	 */
	@Test
	void propertyFull() {
		def changeLog = buildChangeLog {
			property(name: 'emotion', value: 'angry', dbms: 'mysql', context: 'test')
		}

		// change log parameters are not exposed through the API, so get them
		// using reflection.  Also, there are
		def changeLogParameters = changeLog.changeLogParameters
		Field f = changeLogParameters.getClass().getDeclaredField("changeLogParameters")
		f.setAccessible(true)
		def properties = f.get(changeLogParameters)
		def property = properties[properties.size()-1] // The last one is ours.
		assertEquals 'emotion', property.key
		assertEquals 'angry', property.value
		assertEquals 'mysql', property.validDatabases[0]
		assertEquals 'test', property.validContexts[0]
	}

	/**
	 * Try including a property from a file that doesn't exist.
	 */
	@Test(expected = ChangeLogParseException)
	void propertyFromInvalidFile() {
		def changeLog = buildChangeLog {
			property(file: "${TMP_CHANGELOG_DIR}/bad.properties")
		}
	}

	/**
	 * Try including a property from a file when we don't hae a dbms or context.
	 */
	@Test
	void propertyFromFilePartial() {
		def propertyFile = createFileFrom(TMP_CHANGELOG_DIR, '.properties', """
emotion=angry
""")
		propertyFile = propertyFile.canonicalPath
		propertyFile = propertyFile.replaceAll("\\\\", "/")

		def changeLog = buildChangeLog {
			property(file: "${propertyFile}")
		}


		// change log parameters are not exposed through the API, so get them
		// using reflection.  Also, there are
		def changeLogParameters = changeLog.changeLogParameters
		Field f = changeLogParameters.getClass().getDeclaredField("changeLogParameters")
		f.setAccessible(true)
		def properties = f.get(changeLogParameters)
		def property = properties[properties.size()-1] // The last one is ours.
		assertEquals 'emotion', property.key
		assertEquals 'angry', property.value
		assertNull property.validDatabases
		assertNull property.validContexts
	}

	/**
	 * Try including a property from a file when we do have a context and dbms..
	 */
	@Test
	void propertyFromFileFull() {
		def propertyFile = createFileFrom(TMP_CHANGELOG_DIR, '.properties', """
emotion=angry
""")
		propertyFile = propertyFile.canonicalPath
		propertyFile = propertyFile.replaceAll("\\\\", "/")

		def changeLog = buildChangeLog {
			property(file: "${propertyFile}", dbms: 'mysql', context: 'test')
		}


		// change log parameters are not exposed through the API, so get them
		// using reflection.  Also, there are
		def changeLogParameters = changeLog.changeLogParameters
		Field f = changeLogParameters.getClass().getDeclaredField("changeLogParameters")
		f.setAccessible(true)
		def properties = f.get(changeLogParameters)
		def property = properties[properties.size()-1] // The last one is ours.
		assertEquals 'emotion', property.key
		assertEquals 'angry', property.value
		assertEquals 'mysql', property.validDatabases[0]
		assertEquals 'test', property.validContexts[0]
	}

	/**
	 * Helper method that builds a changeSet from the given closure.  Tests will
	 * use this to test parsing the various closures that make up the Groovy DSL.
	 * @param closure the closure containing changes to parse.
	 * @return the changeSet, with parsed changes from the closure added.
	 */
	private def buildChangeLog(Closure closure) {
		def changelog = new DatabaseChangeLog(FILE_PATH)
		changelog.changeLogParameters = new ChangeLogParameters()
		closure.delegate = new DatabaseChangeLogDelegate(changelog)
		closure.delegate.resourceAccessor = resourceAccessor
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()
		return changelog
	}

	private File createFileFrom(directory, suffix, text) {
		createFileFrom(directory, 'liquibase-', suffix, text)
	}

	private File createFileFrom(directory, prefix, suffix, text) {
		def file = File.createTempFile(prefix, suffix, directory)
		file << text
	}
}


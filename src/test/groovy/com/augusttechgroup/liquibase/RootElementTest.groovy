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

package com.augusttechgroup.liquibase

import liquibase.exception.ChangeLogParseException
import liquibase.parser.ChangeLogParserFactory
import liquibase.parser.ChangeLogParser
import liquibase.resource.FileSystemResourceAccessor
import liquibase.changelog.DatabaseChangeLog
import liquibase.precondition.core.PreconditionContainer
import liquibase.precondition.core.PreconditionContainer.FailOption
import liquibase.precondition.core.PreconditionContainer.ErrorOption
import liquibase.precondition.core.PreconditionContainer.OnSqlOutputOption
import liquibase.precondition.core.RunningAsPrecondition
import liquibase.precondition.core.DBMSPrecondition

import liquibase.parser.ext.GroovyLiquibaseChangeLogParser

import org.junit.Test
import org.junit.Before
import static org.junit.Assert.*
import com.augusttechgroup.liquibase.delegate.DatabaseChangeLogDelegate


class RootElementTest {

  final def EMPTY_CHANGELOG = 'src/test/changelog/empty-changelog.groovy'
  final def SIMPLE_CHANGELOG = 'src/test/changelog/simple-changelog.groovy'
  
  def resourceAccessor
  ChangeLogParserFactory parserFactory
  
  
  @Before
  void registerParser() {
    resourceAccessor = new FileSystemResourceAccessor(baseDirectory: '.')
    parserFactory = ChangeLogParserFactory.instance
    ChangeLogParserFactory.getInstance().register(new GroovyLiquibaseChangeLogParser())
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
    def changeLogFile = createFileFrom("""
databaseChangeLog()
""")
    def parser = parserFactory.getParser(changeLogFile.absolutePath, resourceAccessor)
    def changeLog = parser.parse(changeLogFile.absolutePath, null, resourceAccessor)
  }


  @Test
  void parsingDatabaseChangeLogAsProperty() {
    File changeLogFile = createFileFrom("""
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
    def delegate = new DatabaseChangeLogDelegate(databaseChangeLog)
    closure.delegate = delegate
    closure.call()

    def preconditions = databaseChangeLog.preconditions
    assertNotNull preconditions
    assertTrue preconditions instanceof PreconditionContainer
    assertEquals FailOption.WARN, preconditions.onFail
    assertEquals ErrorOption.MARK_RAN, preconditions.onError
    assertEquals OnSqlOutputOption.TEST, preconditions.onSqlOutput
    assertEquals 'fail-message!!!1!!1one!', preconditions.onFailMessage
    assertEquals 'error-message', preconditions.onErrorMessage
  }


  @Test
  void includeChangelog() {
    def includedChangeLogFile = createFileFrom("""
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
	
    def rootChangeLogFile = createFileFrom("""
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
    def rootChangeLog = parser.parse(rootChangeLogFile.absolutePath, null, resourceAccessor)

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
  
  
  private File createFileFrom(text) {
    def file = File.createTempFile('liquibase-', '.groovy')
    file.deleteOnExit()
    file << text
  }
}


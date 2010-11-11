//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

package com.augusttechgroup.liquibase

import liquibase.exception.ChangeLogParseException
import liquibase.parser.ChangeLogParserFactory
import liquibase.resource.FileSystemResourceAccessor
import liquibase.changelog.DatabaseChangeLog
import liquibase.precondition.core.PreconditionContainer
import liquibase.precondition.core.PreconditionContainer.FailOption
import liquibase.precondition.core.PreconditionContainer.ErrorOption
import liquibase.precondition.core.PreconditionContainer.OnSqlOutputOption

import org.junit.Test
import org.junit.Before
import static org.junit.Assert.*
import com.augusttechgroup.liquibase.delegate.DatabaseChangeLogDelegate


class RootElementTests {

  final def EMPTY_CHANGELOG = 'src/test/changelog/empty-changelog.groovy'
  final def SIMPLE_CHANGELOG = 'src/test/changelog/simple-changelog.groovy'
  
  def resourceAccessor
  def parserFactory
  
  
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


  @Test(expected=ChangeLogParseException)
  void parsingClosurelessDatabaseChangeLogFails() {
    def changeLogFile = createFileFrom("""
databaseChangeLog(key: 'value')
""")
    def parser = parserFactory.getParser(changeLogFile.absolutePath, resourceAccessor)
    def changeLog = parser.parse(changeLogFile.absolutePath, null, resourceAccessor)
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

  
  private def createFileFrom(text) {
    def file = File.createTempFile('liquibase-', '.groovy')
    file.deleteOnExit()
    file << text
  }
}


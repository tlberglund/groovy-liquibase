//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the GNU Lesser General Public License v2.1
//

package com.augusttechgroup.liquibase

import com.augusttechgroup.liquibase.GroovyLiquibaseChangeLogParser

import liquibase.parser.ChangeLogParserFactory
import liquibase.resource.FileSystemResourceAccessor
import liquibase.changelog.DatabaseChangeLog

import org.junit.Test
import org.junit.Before
import org.junit.Ignore
import static org.junit.Assert.*


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

}


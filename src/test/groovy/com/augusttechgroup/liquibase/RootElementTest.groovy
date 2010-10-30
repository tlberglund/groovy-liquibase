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
import liquibase.parser.*
import liquibase.resource.FileSystemResourceAccessor

import org.junit.Test
import org.junit.Before
import org.junit.Ignore
import static org.junit.Assert.*

class RootElementTests {

  @Before
  void registerParser() {
    ChangeLogParserFactory.getInstance().register(new GroovyLiquibaseChangeLogParser())
  }
  
  
  @Test
  void parseGroovyChangelog() {
    def changeLogFile = 'src/test/changelog/basic-changelog.groovy'
    def resourceAccessor = new FileSystemResourceAccessor(baseDirectory: '.')
    def parser = ChangeLogParserFactory.getInstance().getParser(changeLogFile, resourceAccessor)

    assertNotNull "Groovy changelog parser was not found", parser

    parser.parse(changeLogFile, null, resourceAccessor)
  }
}
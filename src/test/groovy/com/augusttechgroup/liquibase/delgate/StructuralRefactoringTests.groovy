//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the GNU Lesser General Public License v2.1
//

package com.augusttechgroup.liquibase.delegate

import com.augusttechgroup.liquibase.GroovyLiquibaseChangeLogParser

import liquibase.parser.ChangeLogParserFactory
import liquibase.resource.FileSystemResourceAccessor
import liquibase.changelog.ChangeSet
import liquibase.change.core.AddColumnChange

import org.junit.Test
import org.junit.Before
import org.junit.Ignore
import static org.junit.Assert.*


class StructuralRefactoringTests {

  def resourceAccessor
  def parserFactory
  def changeSet
    

  @Before
  void registerParser() {
    resourceAccessor = new FileSystemResourceAccessor(baseDirectory: '.')
    parserFactory = ChangeLogParserFactory.instance
    ChangeLogParserFactory.getInstance().register(new GroovyLiquibaseChangeLogParser())

		changeSet = new ChangeSet(
		  'generic-changeset-id',
		  'tlberglund',
		  false,
		  false,
		  '/filePath',
		  '/physicalFilePath',
		  'context',
		  'mysql',
		  true)
  }

  
  @Test void addMinimalColumnWithoutConstraints() {
    buildChangeSet {
      addColumn(schemaName: 'oracle_use_only', tableName: 'animal') {
        column(name: 'monkey_status', type: 'varchar(98)')
      }
    }
    
    def changes = changeSet.changes
    assertNotNull changes
    assertEquals 1, changes.size()
    assertTrue changes[0] instanceof AddColumnChange
    assertEquals 'oracle_use_only', changes[0].schemaName
    assertEquals 'animal', changes[0].tableName
    def columns = changes[0].columns
    assertNotNull columns
    assertEquals 1, columns.size()
  }


  private def buildChangeSet(Closure closure) {
    closure.delegate = new ChangeSetDelegate(changeSet: changeSet)
    closure.call()
    changeSet
  }
  
}
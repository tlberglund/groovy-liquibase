//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

package com.augusttechgroup.liquibase.delegate

import com.augusttechgroup.liquibase.GroovyLiquibaseChangeLogParser

import liquibase.parser.ChangeLogParserFactory
import liquibase.resource.FileSystemResourceAccessor
import liquibase.changelog.ChangeSet
import liquibase.change.CheckSum
import liquibase.change.core.RawSQLChange

import org.junit.Test
import org.junit.Before
import org.junit.Ignore
import static org.junit.Assert.*


class ChangeSetMethodTests {

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
  
  
  
  @Test
  void testComments() {
    buildChangeSet {
      comment "This is a comment"
    }
    assertEquals "This is a comment", changeSet.comments
  }


  @Ignore
  @Test
  void testValidChecksum() {
    def checksum = 'd0763edaa9d9bd2a9516280e9044d885'
    def liquibaseChecksum = CheckSum.parse(checksum)
    println "TESTING ${liquibaseChecksum}"
    assertFalse "Arbitrary checksum should not be valid before being added", changeSet.isCheckSumValid(liquibaseChecksum)
    buildChangeSet {
      validCheckSum checksum
    }
    assertTrue "Arbitrary checksum should be valid after being added", changeSet.isCheckSumValid(liquibaseChecksum)
  }
  

  @Test
  void testRollbackString() {
    def rollbackSql = 'DROP TABLE monkey'
    buildChangeSet {
      rollback rollbackSql
    }
    def changes = changeSet.getRollBackChanges()
    assertNotNull changes
    assertEquals 1, changes.size()
    assertEquals(new RawSQLChange(rollbackSql).sql, changes[0].sql)
  }


  @Test void testRollbackClosure() {
    buildChangeSet {
      rollback {
"""UPDATE monkey_table SET emotion='angry' WHERE status='PENDING';
ALTER TABLE monkey_table DROP COLUMN angry;"""
      }
    }
    
    def changes = changeSet.getRollBackChanges()
    assertNotNull changes
    assertEquals 2, changes.size()
    assertEquals(new RawSQLChange("UPDATE monkey_table SET emotion='angry' WHERE status='PENDING'").sql, changes[0].sql)
    assertEquals(new RawSQLChange("ALTER TABLE monkey_table DROP COLUMN angry").sql, changes[1].sql)
  }
  

  private def buildChangeSet(Closure closure) {
    closure.delegate = new ChangeSetDelegate(changeSet: changeSet)
    closure.call()
    changeSet
  }  
  
}
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

import org.junit.Before
import liquibase.changelog.ChangeSet
import java.sql.Timestamp
import java.text.SimpleDateFormat


class ChangeSetTests
{
  def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  def changeSet

  @Before
  void createChangeSet() {
		changeSet = new ChangeSet(
		  'generic-changeset-id',
		  'tlberglund',
		  false,
		  false,
		  '/filePath',
		  'context',
		  'mysql',
		  true)
  }

  
  def buildChangeSet(Closure closure) {
    closure.delegate = new ChangeSetDelegate(changeSet: changeSet)
    closure.call()
    changeSet
  }


  Timestamp parseSqlTimestamp(dateTimeString) {
    new Timestamp(sdf.parse(dateTimeString).time)
  }
}
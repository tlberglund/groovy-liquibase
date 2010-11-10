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


import liquibase.changelog.ChangeSet


class DatabaseChangeLogDelegate {
  def databaseChangeLog
  def params


  DatabaseChangeLogDelegate(databaseChangeLog) {
    this([:], databaseChangeLog)
  }

  
  DatabaseChangeLogDelegate(Map params, databaseChangeLog) {
    this.params = params
    this.databaseChangeLog = databaseChangeLog
    params.each { key, value ->
      databaseChangeLog[key] = value
    }
  }
  
  
  void changeSet(Map params, closure) {
		def changeSet = new ChangeSet(
		  params.id,
		  params.author,
		  params.alwaysRun?.toBoolean() ?: false,
		  params.runOnChange?.toBoolean() ?: false,
		  params.filePath,
		  databaseChangeLog.getPhysicalFilePath(),
		  params.context,
		  params.dbms,
		  params.runInTransaction?.toBoolean() ?: true)
		  
		if(params.failOnError) {
  		changeSet.failOnError = params.failOnError?.toBoolean()
		}

		if(params.onValidationFail) {
  		changeSet.onValidationFail = ChangeSet.ValidationFailOption.valueOf(params.onValidationFail)
		}
		
		def delegate = new ChangeSetDelegate(changeSet: changeSet)
		closure.delegate = delegate
		
		closure.call()
		
		databaseChangeLog.addChangeSet(changeSet)
  }


  void preConditions(Map params = [:], Closure closure) {
    def delegate = new PreconditionDelegate(params)
    closure.call()
    databaseChangeLog.preconditions = delegate.preconditions
  }

}

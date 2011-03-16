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


import java.util.Map;

import liquibase.changelog.ChangeSet
import liquibase.precondition.core.PreconditionContainer.OnSqlOutputOption
import liquibase.precondition.core.PreconditionContainer.ErrorOption
import liquibase.precondition.core.PreconditionContainer.FailOption
import liquibase.precondition.core.PreconditionContainer
import liquibase.parser.ChangeLogParserFactory


class DatabaseChangeLogDelegate {
  def databaseChangeLog
  def params
  def resourceAccessor


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
      databaseChangeLog.physicalFilePath,
      params.context,
      params.dbms,
      params.runInTransaction?.toBoolean() ?: true)

    if(params.failOnError) {
      changeSet.failOnError = params.failOnError?.toBoolean()
    }

    if(params.onValidationFail) {
      changeSet.onValidationFail = ChangeSet.ValidationFailOption.valueOf(params.onValidationFail)
    }

    def delegate = new ChangeSetDelegate(changeSet: changeSet,
                                         databaseChangeLog: databaseChangeLog,
                                         resourceAccessor: resourceAccessor)
    closure.delegate = delegate
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure.call()
    
    databaseChangeLog.addChangeSet(changeSet)
  }


  void preConditions(Map params = [:], Closure closure) {
    databaseChangeLog.preconditions = PreconditionDelegate.buildPreconditionContainer(params, closure)
  }


  void include(Map params = [:]) {
	  def files = []

	  if(params.path){
		  // pass a directory
		  def includedChangeLogPath = params.path
		  
		  // get all files ending with .groovy
		  new File(includedChangeLogPath).eachFileMatch(~/.*.groovy/) { file->
			  files << file.getPath()
		  }
		  
		  // order the files, alphabetic
		  files.sort()
		  
	  }else if (params.file){
		  // pass a file
		  files << params.file
	  }
	  files.each {
		  def parser = ChangeLogParserFactory.getInstance().getParser(it, resourceAccessor)
		  def includedChangeLog = parser.parse(it, null, resourceAccessor)
		  includedChangeLog?.changeSets.each { changeSet ->
			  databaseChangeLog.addChangeSet(changeSet)
		  }
		  includedChangeLog?.preconditionContainer?.nestedPreconditions.each { precondition ->
			  databaseChangeLog.preconditionContainer.addNestedPrecondition(precondition)
		  }
	  }
  }


  void property(Map params = [:]) {
    
  }

}

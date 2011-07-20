//
// com.augusttechgroup.liquibase.delegate
// Copyright (C) 2011 
// ALL RIGHTS RESERVED
//

package com.augusttechgroup.liquibase.delegate

import com.augusttechgroup.liquibase.change.GroovyChange
import liquibase.changelog.ChangeSet
import liquibase.resource.ResourceAccessor
import liquibase.database.Database
import liquibase.database.DatabaseConnection
import java.sql.Connection
import groovy.sql.Sql


/**
 * <p></p>
 * 
 * @author Tim Berglund
 */
class GroovyChangeDelegate
{
  GroovyChange change
  Closure initClosure
  Closure validateClosure
  Closure changeClosure
  Closure rollbackClosure
  String confirmationMessage
  String checksum

  ChangeSet changeSet
  ResourceAccessor resourceAccessor
  Database database
  DatabaseConnection databaseConnection
  Connection connection
  Sql sql


  GroovyChangeDelegate(Closure groovyChangeClosure,
                       ChangeSet changeSet,
                       ResourceAccessor resourceAccessor) {
    this.changeSet = changeSet
    this.resourceAccessor = resourceAccessor
//    change = new GroovyChange(groovyChangeClosure)
  }


  def init(Closure c) {
    c.delegate = this
    initClosure = c
  }


  def validate(Closure c) {
    c.delegate = this
    validateClosure = c
  }


  def change(Closure c) {
    c.delegate = this
    changeClosure = c
  }


  def rollback(Closure c) {
    c.delegate = this
    rollbackClosure = c
  }


  def confirm(String message) {
    c.delegate = this
    confirmationMessage = message
  }


  def checkSum(String checkSum) {
    c.delegate = this
    this.checksum = checkSum
  }

}
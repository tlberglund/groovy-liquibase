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
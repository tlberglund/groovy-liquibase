//
// com.augusttechgroup.liquibase.delegate
// Copyright (C) 2011 
// ALL RIGHTS RESERVED
//

package com.augusttechgroup.liquibase.delegate

import com.augusttechgroup.liquibase.change.GroovyChange
import liquibase.changelog.ChangeSet
import liquibase.resource.ResourceAccessor

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


  GroovyChangeDelegate(Closure groovyChangeClosure) {
    change = new GroovyChange()
  }


  def init(Closure c) {
    initClosure = c
  }


  def validate(Closure c) {
    validateClosure = c
  }


  def change(Closure c) {
    changeClosure = c
  }


  def rollback(Closure c) {
    rollbackClosure = c
  }


  def confirm(String message) {
    confirmationMessage = message
  }


  def checkSum(String checkSum) {
    this.checksum = checkSum
  }


}
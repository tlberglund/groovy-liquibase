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

import liquibase.exception.ChangeLogParseException
import liquibase.precondition.core.AndPrecondition
import liquibase.precondition.core.OrPrecondition
import liquibase.precondition.core.SqlPrecondition
import liquibase.precondition.CustomPreconditionWrapper
import liquibase.precondition.PreconditionFactory
import liquibase.precondition.core.NotPrecondition;
import liquibase.precondition.core.PreconditionContainer
import liquibase.precondition.core.PreconditionContainer.OnSqlOutputOption
import liquibase.precondition.core.PreconditionContainer.ErrorOption
import liquibase.precondition.core.PreconditionContainer.FailOption
import liquibase.util.ObjectUtil;


class PreconditionDelegate {
  def preconditions = []
  def databaseChangeLog
	def changeSetId = '<unknown>' // used for error messages



	/**
	 * Handle all non-nesting preconditions using the PreconditionFactory.
	 * @param name the name of the precondition to create
	 * @param args the attributes of the new precondition
	 */
  void methodMissing(String name, args) {
    def preconditionFactory = PreconditionFactory.instance
	  def precondition = null
	  try {
      precondition = preconditionFactory.create(name)
	  } catch (RuntimeException e) {
		  throw new ChangeLogParseException("ChangeSet '${changeSetId}': '${name}' is an invalid precondition.", e)
	  }
    def params = args[0]

    if ( params != null && params instanceof Map ) {
      params.each { key, value ->
	      try {
          ObjectUtil.setProperty(precondition, key, DelegateUtil.expandExpressions(value, databaseChangeLog))
	      } catch (RuntimeException e) {
		      throw new ChangeLogParseException("ChangeSet '${changeSetId}': '${key}' is an invalid property for '${name}' preconditions.", e)
	      }
      }
    }

    preconditions << precondition
  }

	/**
	 * Create a sqlCheck precondition.  This one needs some special handling
	 * because the SQL is in a nested closure.
	 * @param params the attribures of the precondition
	 * @param closure the SQL for the precondition
	 * @return the newly created precondition.
	 */
  def sqlCheck(Map params = [:], Closure closure) {
    def precondition = new SqlPrecondition()
	  params.each { key, value ->
		  try {
			  ObjectUtil.setProperty(precondition, key, DelegateUtil.expandExpressions(value, databaseChangeLog))
		  } catch (RuntimeException e) {
			  throw new ChangeLogParseException("ChangeSet '${changeSetId}': '${key}' is an invalid property for 'sqlCheck' preconditions.", e)
		  }
	  }

	  def sql = DelegateUtil.expandExpressions(closure.call(), databaseChangeLog)
	  if ( sql != null && sql != "null" ) {
		  precondition.sql = sql
	  }
    preconditions << precondition
  }

	/**
	 * Create a customPrecondition.  A custom precondition is a class that
	 * implements the Liquibase customPrecondition.  The code can do anything
	 * we want.  Parameters need to be passed to our custom class as key/value
	 * pairs, either with the XML style of nested {@code param} blocks, or by
	 * calling nested methods where the name of the method becomes the key and the
	 * arguments become the value.
	 * @param params the params for the precondition, such as the class name.
	 * @param closure the closure with nested key/value pairs for the custom
	 *        precondition.
	 */
  def customPrecondition(Map params = [:], Closure closure) {
    def delegate = new KeyValueDelegate(changeSetId: changeSetId)
    closure.delegate = delegate
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure.call()

    def precondition = new CustomPreconditionWrapper()
	  params.each { key, value ->
		  try {
			  ObjectUtil.setProperty(precondition, key, DelegateUtil.expandExpressions(value, databaseChangeLog))
		  } catch (RuntimeException e) {
			  throw new ChangeLogParseException("ChangeSet '${changeSetId}': '${key}' is an invalid property for 'customPrecondition' preconditions.", e)
		  }
	  }
    delegate.map.each { key, value ->
	    // This is a key/value pair in the Liquibase object, so it won't fail.
      precondition.setParam(key, DelegateUtil.expandExpressions(value, databaseChangeLog))
    }

    preconditions << precondition
  }

  
  def and(Closure closure) {
    def precondition = nestedPrecondition(AndPrecondition, closure)
    preconditions << precondition
  }


  def or(Closure closure) {
    def precondition = nestedPrecondition(OrPrecondition, closure)
    preconditions << precondition
  }
  
  def not(Closure closure) {
    def precondition = nestedPrecondition(NotPrecondition, closure)
    preconditions << precondition
  }

	/**
	 * execute a {@code preconditions} closure and return the Liquibase
	 * {@code PreconditionContainer} it creates.
	 * @param databaseChangeLog the database changelog that owns the changesets.
	 * @param changeSetId the id of the changeset that owns the precondtions
	 * @param params the parameters to the preconditions
	 * @param closure nested closures to call.
	 * @return the PreconditionContainer it builds.
	 */
  static PreconditionContainer buildPreconditionContainer(databaseChangeLog, changeSetId, Map params, Closure closure) {
    def preconditions = new PreconditionContainer()

	  // Process parameters.  3 of them need a special case.
	  params.each { key, value ->
		  def paramValue = DelegateUtil.expandExpressions(value, databaseChangeLog)
		  if ( key == "onFail" ) {
			  preconditions.onFail = FailOption."${paramValue}"
		  } else if ( key == "onError" ) {
			  preconditions.onError = ErrorOption."${paramValue}"
		  } else if ( key == "onUpdateSQL" ) {
			  preconditions.onSqlOutput = OnSqlOutputOption."${paramValue}"
		  } else {
			  // pass the reset to Liquibase
			  try {
				  ObjectUtil.setProperty(preconditions, key, paramValue)
			  } catch (RuntimeException e) {
				  throw new ChangeLogParseException("ChangeSet '${changeSetId}': '${key}' is an invalid property for preconditions.", e)
			  }
		  }
	  }

    def delegate = new PreconditionDelegate(databaseChangeLog: databaseChangeLog,
                                            changeSetId: changeSetId)
    closure.delegate = delegate
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure.call()

    delegate.preconditions.each { precondition ->
      preconditions.addNestedPrecondition(precondition)
    }

    return preconditions
  }


  private def nestedPrecondition(Class preconditionClass, Closure closure) {
      
    def nestedPrecondition = preconditionClass.newInstance()
    def delegate = new PreconditionDelegate(databaseChangeLog: databaseChangeLog,
                                            changeSetId: changeSetId)
    closure.delegate = delegate
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure.call()

    delegate.preconditions.each { precondition ->
      nestedPrecondition.addNestedPrecondition(precondition)
    }

    return nestedPrecondition
  }
}
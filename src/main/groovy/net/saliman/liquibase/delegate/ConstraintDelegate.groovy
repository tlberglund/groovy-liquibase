/*
 * Copyright 2011-2014 Tim Berglund and Steven C. Saliman
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

package net.saliman.liquibase.delegate

import liquibase.change.ConstraintsConfig
import liquibase.exception.ChangeLogParseException
import liquibase.util.ObjectUtil;


class ConstraintDelegate {
  def constraint
  def databaseChangeLog
	def changeSetId = '<unknown>' // used for error messages
	def changeName = '<unknown>' // used for error messages


  ConstraintDelegate() {
    constraint = new ConstraintsConfig()
  }


  def constraints(Map params = [:]) {
    params.each { key, value ->
      try {
	      ObjectUtil.setProperty(constraint, key, DelegateUtil.expandExpressions(value, databaseChangeLog))
      } catch(RuntimeException e) {
	      // Rethrow as an ChangeLogParseException with a more helpful message
	      // than you'll get from the Liquibase helper.
	      throw new ChangeLogParseException("ChangeSet '${changeSetId}': '${key}' is not a valid constraint attribute for '${changeName}' changes.", e)
      }
    }
  }

	/**
	 * This method is only here to provide a sane error message during a
	 * transition period, and will be removed in a future release.
	 * @param closure
	 */
	@Deprecated
  def constraints(Closure closure) {
		// this is not how the XML works, and we don't do this anywhere else so
		// deprecate it.
		throw new ChangeLogParseException("Error: ChangeSet '${changeSetId}', ${changeName} change: Setting constraint attributes in nested closures is no longer supported. Use an attribute map instead.")
  }
  

  def methodMissing(String name, params) {
    if ( constraint.hasProperty(name) ) {
      ObjectUtil.setProperty(constraint, name, DelegateUtil.expandExpressions(params[0], databaseChangeLog))
    } else {
	    throw new ChangeLogParseException("ChangeSet '${changeSetId}': '${name}' is not a valid child element of constraint closures in ${changeName} changes")
    }
  }
  
}
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

import liquibase.change.ConstraintsConfig
import liquibase.util.ObjectUtil;


class ConstraintDelegate {
  def constraint
  def databaseChangeLog


  ConstraintDelegate() {
    constraint = new ConstraintsConfig(primaryKey: false,
                                       primaryKeyName: null,
                                       primaryKeyTablespace: null,
                                       foreignKeyName: null,
                                       references: null,
				                               referencedTableName: null,
				                               referencedColumnNames: null,
                                       checkConstraint: null,
                                       unique: false,
                                       uniqueConstraintName: null,
                                       deleteCascade: false,
                                       initiallyDeferred: false,
                                       deferrable: false,
                                       nullable: true)
  }


  def constraints(Map params = [:]) {
    params.each { key, value ->
      ObjectUtil.setProperty(constraint, key, expandExpressions(value))
    }
  }


  def constraints(Closure closure) {
    closure.delegate = this
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure.call()
  }
  

  def methodMissing(String name, params) {
    if(constraint.hasProperty(name)) {
      ObjectUtil.setProperty(constraint, name, expandExpressions(params[0]))
    }
  }
  
  private def expandExpressions(expression) {
    databaseChangeLog.changeLogParameters.expandExpressions(expression.toString())
  }
}
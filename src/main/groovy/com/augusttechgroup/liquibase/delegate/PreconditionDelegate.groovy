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


class PreconditionDelegate
{
  def preconditions = []
  def databaseChangeLog
  

  //
  // Handles all non-nesting preconditions using the PreconditionFactory.
  //
  void methodMissing(String name, args) {
    def preconditionFactory = PreconditionFactory.instance
    def precondition = preconditionFactory.create(name)
    def params = args[0]

    if(params != null && params instanceof Map) {
      params.each { key, value ->
        ObjectUtil.setProperty(precondition, key, expandExpressions(value))
      }
    }

    preconditions << precondition
  }


  def sqlCheck(Map params = [:], Closure closure) {
    def precondition = new SqlPrecondition()
    precondition.expectedResult = expandExpressions(params.expectedResult)
    precondition.sql = expandExpressions(closure.call())
    preconditions << precondition
  }


  def customPrecondition(Map params = [:], Closure closure) {
    def delegate = new KeyValueDelegate()
    closure.delegate = delegate
    closure.resolveStrategy = Closure.DELEGATE_ONLY
    closure.call()

    def precondition = new CustomPreconditionWrapper()
    precondition.className = params.className
    delegate.map.each { key, value ->
      precondition.setParam(key, expandExpressions(value))
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
  

  static PreconditionContainer buildPreconditionContainer(databaseChangeLog, Map params, Closure closure) {
    def preconditions = new PreconditionContainer()

    if(params.onFail) {
      preconditions.onFail = FailOption."${params.onFail}"
    }

    if(params.onError) {
      preconditions.onError = ErrorOption."${params.onError}"
    }

    if(params.onUpdateSQL) {
      preconditions.onSqlOutput = OnSqlOutputOption."${params.onUpdateSQL}"
    }

    preconditions.onFailMessage = params.onFailMessage
    preconditions.onErrorMessage = params.onErrorMessage

    def delegate = new PreconditionDelegate(databaseChangeLog: databaseChangeLog)
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
    def delegate = new PreconditionDelegate(databaseChangeLog: databaseChangeLog)
    closure.delegate = delegate
    closure.resolveStrategy = Closure.DELEGATE_ONLY
    closure.call()

    delegate.preconditions.each { precondition ->
      nestedPrecondition.addNestedPrecondition(precondition)
    }

    return nestedPrecondition
  }
  
  private def expandExpressions(expression) {
    databaseChangeLog.changeLogParameters.expandExpressions(expression.toString())
  }

}
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

import liquibase.change.ColumnConfig
import liquibase.util.ObjectUtil;


class ColumnDelegate {
  def columns = []
  def columnConfigClass = ColumnConfig
  def whereClause
  def databaseChangeLog


  //
  // This arguably does not cohere with the overall purpose of the class,
  // but in the whole entire DSL, where clauses pretty much only occur in
  // column-y (not to be confused with calumny) places.
  //
  def where(String whereClause) {
    this.whereClause = expandExpressions(whereClause)
  }


  def column(Map params, Closure closure = null) {
    def column = columnConfigClass.newInstance()

    params.each { key, value ->
      ObjectUtil.setProperty(column, key, expandExpressions(value)) 
    }

    if(closure) {
      def constraintDelegate = new ConstraintDelegate(databaseChangeLog: databaseChangeLog)
      closure.delegate = constraintDelegate
      closure.call()
      column.constraints = constraintDelegate.constraint
    }

    columns << column
  }
  
  private def expandExpressions(expression) {
    databaseChangeLog.changeLogParameters.expandExpressions(expression.toString())
  }

}
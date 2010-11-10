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

import liquibase.change.ColumnConfig


class ColumnDelegate {
  def columns = []
  def columnConfigClass = ColumnConfig
  def whereClause


  //
  // This arguably does not cohere with the overall purpose of the class,
  // but in the whole entire DSL, where clauses pretty much only occur in
  // column-y (not to be confused with calumny) places.
  //
  def where(String whereClause) {
    this.whereClause = whereClause
  }


  def column(Map params, Closure closure = null) {
    def column = columnConfigClass.newInstance()

    params.each { key, value ->
      column[key] = value
    }

    if(closure) {
      def constraintDelegate = new ConstraintDelegate()
      closure.delegate = constraintDelegate
      closure.call()
      column.constraints = constraintDelegate.constraint
    }

    columns << column
  }

}
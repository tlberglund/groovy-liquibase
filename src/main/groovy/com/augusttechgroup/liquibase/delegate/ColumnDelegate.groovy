//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the GNU Lesser General Public License v2.1
//

package com.augusttechgroup.liquibase.delegate

import liquibase.change.ColumnConfig


class ColumnDelegate {
  def columns = []
  def columnConfigClass = ColumnConfig


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
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


  def column(Map params, Closure closure = null) {
    def column = new ColumnConfig()

    params.each { key, value ->
      column[key] = value
    }

    columns << column
  }

}
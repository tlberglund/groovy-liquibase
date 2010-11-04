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

import liquibase.change.ConstraintsConfig


class ConstraintDelegate {
  def constraint


  ConstraintDelegate() {
    constraint = new ConstraintsConfig(primaryKey: false,
                                       unique: false,
                                       deleteCascade: false,
                                       initiallyDeferred: false,
                                       deferrable: false,
                                       nullable: true)
  }


  def constraint(Map params = [:]) {
    params.each { key, value ->
      constraint[key] = value
    }
  }


  def constraint(Closure closure) {
    closure.delegate = this
    closure.call()
  }


  def methodMissing(String name, params) {
    if(constraint.hasProperty(name)) {
      constraint[name] = params[0]
    }
  }
}
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

import liquibase.precondition.core.PreconditionContainer
import liquibase.precondition.core.PreconditionContainer.FailOption
import liquibase.precondition.core.PreconditionContainer.ErrorOption
import liquibase.precondition.core.PreconditionContainer.OnSqlOutputOption

/**
 * <p></p>
 * 
 * @author Tim Berglund
 */
class PreconditionDelegate
{
  def preconditions


  PreconditionDelegate(Map params) {
    preconditions = new PreconditionContainer()

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
  }
}
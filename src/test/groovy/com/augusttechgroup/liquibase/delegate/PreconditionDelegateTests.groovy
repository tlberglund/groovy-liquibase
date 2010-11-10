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

import org.junit.Test
import static org.junit.Assert.*
import liquibase.precondition.core.PreconditionContainer
import liquibase.precondition.core.PreconditionContainer.FailOption
import liquibase.precondition.core.PreconditionContainer.ErrorOption
import liquibase.precondition.core.PreconditionContainer.OnSqlOutputOption


class PreconditionDelegateTests
{

  @Test
  void preconditionParameterOptions() {
    def params = [onFail: 'WARN', onError: 'HALT', onUpdateSQL: 'IGNORE', onFailMessage: 'fail-message!!!1!!1one!', onErrorMessage: 'error-message']
    def delegate = new PreconditionDelegate(params)
    def preconditions = delegate.preconditions

    assertNotNull preconditions
    assertTrue preconditions instanceof PreconditionContainer
    assertEquals FailOption.WARN, preconditions.onFail
    assertEquals ErrorOption.HALT, preconditions.onError
    assertEquals OnSqlOutputOption.IGNORE, preconditions.onSqlOutput
    assertEquals 'fail-message!!!1!!1one!', preconditions.onFailMessage
    assertEquals 'error-message', preconditions.onErrorMessage
  }


}
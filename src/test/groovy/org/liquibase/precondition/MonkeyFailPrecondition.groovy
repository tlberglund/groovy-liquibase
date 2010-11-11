//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

package org.liquibase.precondition

import liquibase.precondition.CustomPrecondition
import liquibase.database.Database
import liquibase.exception.CustomPreconditionFailedException


class MonkeyFailPrecondition
  implements CustomPrecondition
{

  void check(Database database) {
    throw new CustomPreconditionFailedException('Stub precondition failed')
  }
}
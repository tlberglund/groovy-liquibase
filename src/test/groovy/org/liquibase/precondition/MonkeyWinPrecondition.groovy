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


class MonkeyWinPrecondition
  implements CustomPrecondition
{

  void check(Database database) {
    // Do nothing, and the precondition passes
  }
}
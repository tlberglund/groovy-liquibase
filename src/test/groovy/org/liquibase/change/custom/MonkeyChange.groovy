//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the GNU Lesser General Public License v2.1
//

package org.liquibase.change.custom

import liquibase.change.custom.CustomChange
import liquibase.resource.ResourceAccessor
import liquibase.exception.ValidationErrors
import liquibase.database.Database

/**
 * A dummy change class for unit testing of the custom change mechanism.
 * 
 * @author Tim Berglund
 */
class MonkeyChange
  implements CustomChange
{

  String getConfirmationMessage() {
    "MonkeyChange confirmed"
  }


  void setUp() {
    
  }


  void setFileOpener(ResourceAccessor resourceAccessor) {

  }


  ValidationErrors validate(Database database) {
    null
  }
}
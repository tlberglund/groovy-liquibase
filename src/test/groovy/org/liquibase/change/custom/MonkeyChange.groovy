/*
 * Copyright 2011-2015 Tim Berglund and Steven C. Saliman
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
class MonkeyChange implements CustomChange {

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
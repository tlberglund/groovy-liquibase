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
package org.liquibase.groovy.custom

import liquibase.change.custom.CustomSqlChange
import liquibase.database.Database
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor
import liquibase.statement.SqlStatement
import liquibase.statement.core.RawSqlStatement

/**
 * A trivial liquibase CustomSqlChange that will be added to the DSL 
 * through groovy metaprogramming
 * 
 * @see groovy.runtime.metaclass.org.liquibase.groovy.delegate.ChangeSetDelegateMetaClass
 * @author Jason Clawson
 */
class MyCustomSqlChange 
  implements CustomSqlChange {

  String getConfirmationMessage() {
    return 'confirmation message here'
  }

  void setUp() {
    ;
  }

  public void setFileOpener(ResourceAccessor resourceAccessor) {
    ;
  }

  ValidationErrors validate(Database database) {
    new ValidationErrors()
  }

  SqlStatement[] generateStatements(Database database) {
    [ new RawSqlStatement("SELECT * FROM monkey") ]
  }

}

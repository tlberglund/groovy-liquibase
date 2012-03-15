

package com.augusttechgroup.liquibase.custom

import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;


/**
 * A trivial liquibase CustomSqlChange that will be added to the DSL 
 * through groovy metaprogramming
 * 
 * @see groovy.runtime.metaclass.com.augusttechgroup.liquibase.delegate.ChangeSetDelegateMetaClass 
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

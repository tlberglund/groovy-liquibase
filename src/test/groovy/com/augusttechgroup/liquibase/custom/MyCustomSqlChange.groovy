

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
class MyCustomSqlChange implements CustomSqlChange {

	public String getConfirmationMessage() {
		return "confirmation message here";
	}

	public void setUp() throws SetupException {}

	public void setFileOpener(ResourceAccessor resourceAccessor) {}

	public ValidationErrors validate(Database database) {
		return new ValidationErrors();
	}

	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		return [
                new RawSqlStatement("SELECT * FROM monkey")
        ];
	}

}

package net.saliman.liquibase.custom

import liquibase.change.custom.CustomChange
import liquibase.database.Database
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor
import liquibase.statement.SqlStatement
import liquibase.statement.core.RawSqlStatement

/**
 * A trivial liquibase CustomSqlChange that will be added to the DSL 
 * through groovy metaprogramming
 *
 * @see groovy.runtime.metaclass.net.saliman.liquibase.delegate.ChangeSetDelegateMetaClass
 * @author Jason Clawson
 */
class MyParametrizedCustomChange implements CustomChange {

    def myProperty

    MyParametrizedCustomChange() {
        super()
    }

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
        [new RawSqlStatement("SELECT * FROM monkey where property= ${myProperty}")]
    }

}

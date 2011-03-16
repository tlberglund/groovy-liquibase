//
// com.augusttechgroup.liquibase.change
// Copyright (C) 2011 
// ALL RIGHTS RESERVED
//

package com.augusttechgroup.liquibase.change

import liquibase.change.AbstractChange
import liquibase.statement.SqlStatement
import liquibase.database.Database

/**
 * <p></p>
 * 
 * @author Tim Berglund
 */
class GroovyChange
  extends AbstractChange
{

  GroovyChange(groovyChangeClosure) {
    this.groovyChangeClosure = groovyChangeClosure
  }

  
  String getConfirmationMessage() {
    "Custom Groovy change executed"
  }

  
  SqlStatement[] generateStatements(Database database) {
    return new SqlStatement[0];  //To change body of implemented methods use File | Settings | File Templates.
  }
}
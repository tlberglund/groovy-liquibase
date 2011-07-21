/*
 * Copyright 2011 Tim Berglund
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
  def groovyChangeClosure

  
  GroovyChange(groovyChangeClosure) {
    super()
    this.groovyChangeClosure = groovyChangeClosure
  }

  
  String getConfirmationMessage() {
    "Custom Groovy change executed"
  }

  
  SqlStatement[] generateStatements(Database database) {
    return new SqlStatement[0];  //To change body of implemented methods use File | Settings | File Templates.
  }
}
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
package org.liquibase.groovy.change

import liquibase.change.AbstractChange
import liquibase.change.ChangeMetaData
import liquibase.change.custom.*
import liquibase.database.Database
import liquibase.exception.CustomChangeException
import liquibase.exception.RollbackImpossibleException
import liquibase.exception.ValidationErrors
import liquibase.exception.Warnings
import liquibase.statement.SqlStatement

/**
 * Based on liquibase.change.custom.CustomChangeWrapper but more friendly to 
 * programmatic changes instead of XML driven changes
 * 
 * @author Jason Clawson
 */
class CustomProgrammaticChangeWrapper
  extends AbstractChange {

  final CustomChange customChange

  CustomProgrammaticChangeWrapper(CustomChange change) {
    super()
    this.customChange = change
  }

  @Override
  String getConfirmationMessage() {
    customChange.getConfirmationMessage()
  }

  @Override
  SqlStatement[] generateStatements(Database database) {
    configureCustomChange()
    if(customChange instanceof CustomSqlChange) {
      return customChange.generateStatements(database)
    }
    else if(customChange instanceof CustomTaskChange) {
      customChange.execute(database)
    }

    //doesn't provide any sql statements to execute
    return []
  }

  @Override
  ValidationErrors validate(Database database) {
    try {
      return customChange.validate(database)
    }
    catch (AbstractMethodError e) {
      return new ValidationErrors()
    }
  }

  @Override
  SqlStatement[] generateRollbackStatements(Database database) {
    if(supportsRollback(database)) {
      try {
        configureCustomChange()
        if(customChange instanceof CustomSqlChange) {
          return customChange.generateRollbackStatements(database)
        }
        else if (customChange instanceof CustomTaskRollback) {
          customChange.rollback(database)
        }
      } 
      catch(CustomChangeException e) {
        throw new RollbackImpossibleException(e)
      }
    }

    //doesn't provide any sql statements to execute
    return []
  }

  @Override
  public boolean supportsRollback(Database database) {
    return customChange instanceof CustomSqlRollback || customChange instanceof CustomTaskRollback;
  }

  @Override
  public Warnings warn(Database database) {
    //does not support warns
    return new Warnings();
  }

  private void configureCustomChange() {
    try {
      customChange.setFileOpener(getResourceAccessor())
      customChange.setUp()
    } 
    catch (Exception e) {
      throw new CustomChangeException(e)
    }
  }
}

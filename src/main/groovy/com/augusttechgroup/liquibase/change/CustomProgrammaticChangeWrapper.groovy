package com.augusttechgroup.liquibase.change

import liquibase.change.AbstractChange
import liquibase.change.ChangeMetaData
import liquibase.change.custom.CustomChange
import liquibase.change.custom.CustomSqlChange
import liquibase.change.custom.CustomSqlRollback
import liquibase.change.custom.CustomTaskChange
import liquibase.change.custom.CustomTaskRollback
import liquibase.database.Database
import liquibase.exception.CustomChangeException
import liquibase.exception.RollbackImpossibleException
import liquibase.exception.UnsupportedChangeException
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
    super('customChange', 'Custom Change', ChangeMetaData.PRIORITY_DEFAULT)
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

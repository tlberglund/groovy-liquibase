//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the GNU Lesser General Public License v2.1
//

package com.augusttechgroup.liquibase.delegate

import liquibase.changelog.ChangeSet


class ChangeSetDelegate {
  def changeSet

  void comment(String text) {
    changeSet.comments = text
  }
  
  void preConditions(Closure closure) {

  }
  
  //TODO Verify that this works. Don't fully understand addValidCheckSum() yet...
  void validCheckSum(String checksum) {
    println "ADDING ${checksum} to ${changeSet}"
    changeSet.addValidCheckSum(checksum)
  }
  
  void rollback(String sql) {
    changeSet.addRollBackSQL(sql)
  }

  void rollback(Closure closure) {
    changeSet.addRollBackSQL(closure.call().toString())
  }
  
  void rollback(Map params) {
    //TODO implement after changeSet processing is substantially implemented (testing requires it)
  }
  
  void addColumn(Map params, Closure closure) {
    
  }
  
  void renameColumn(Map params) {
    
  }
  
  void modifyColumn(Map params, Closure closure) {
    
  }
  
  void dropColumn(Map params) {
    
  }
  
  void alterSequence(Map params) {
    
  }
  
  void createTable(Map params, Closure closure) {
    
  }
  
  void renameTable(Map params) {
    
  }
  
  void dropTable(Map params) {
    
  }
  
  void createView(Map params, Closure closure) {
    
  }
  
  void renameView(Map params) {
    
  }
  
  void dropView(Map params) {
    
  }
  
  void mergeColumns(Map params) {
    
  }
  
  void createStoredProcedure(String storedProc) {
    
  }
  
  void addLookupTable(Map params) {
    
  }
  
  void addNotNullConstrains(Map params) {
    
  }
  
  void dropNotNullConstraint(Map params) {
    
  }
  
  void addUniqueConstraint(Map params) {
    
  }
  
  void dropUniqueConstraint(Map params) {
    
  }
  
  void createSequence(Map params) {
    
  }
  
  void dropSequence(Map params) {
    
  }
  
  void addAutoIncrement(Map params) {
    
  }
  
  void addDefaultValue(Map params) {
    
  }
  
  void dropDefaultValue(Map params) {
    
  }
  
  void addForeignKeyConstraint(Map params) {
    
  }
  
  void dropForeignKeyConstraint(Map params) {
    
  }
  
  void addPrimaryKey(Map params) {
    
  }
  
  void dropPrimaryKey(Map params) {
    
  }
  
  void insert(Map params, Closure closure) {
    
  }
  
  void loadData(Map params, Closure clousre) {
    
  }
  
  void loadUpdateData(Map params, Closure closure) {
    
  }
  
  void update(Map params, Closure closure) {
    
  }
  
  void delete(Map params, Closure closure) {
    
  }
  
  void tagDatabase(Map params) {
    
  }
  
  void stop(Map params) {
    
  }
  
  void createIndex(Map params, Closure closure) {
    
  }
  
  void dropIndex(Map params) {
    
  }
  
  void sql(Map params, Closure closure) {
    
  }
  
  void sqlFile(Map params) {
    
  }
  
  void customChange(Map params, Closure closure) {
    
  }

  /**
   * A Groovy-specific extension that allows a closure to be provided,
   * implementing the change. The closure is passed the instance of
   * Database.
   */
  void customChange(Closure closure) {
    
  }
  
  void executeCommand(Map params, Closure closure) {
    
  }
  
}

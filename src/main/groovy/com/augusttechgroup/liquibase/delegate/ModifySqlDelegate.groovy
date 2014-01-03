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

package com.augusttechgroup.liquibase.delegate

import liquibase.exception.ChangeLogParseException
import liquibase.sql.visitor.SqlVisitor;
import liquibase.sql.visitor.SqlVisitorFactory;
import liquibase.changelog.ChangeSet


class ModifySqlDelegate {
  def modifySqlDbmsList
  def modifySqlAppliedOnRollback
  def modifySqlContexts
  def sqlVisitors = []
  def changeSet

  
  ModifySqlDelegate(Map params = [:], ChangeSet changeSet) {
	if(params.dbms){
	  modifySqlDbmsList = params.dbms.replaceAll(" ", "").split(',') as Set
	}
	if(params.context){
	  modifySqlContexts = params.context.replaceAll(" ", "").split(',') as Set
	}
	modifySqlAppliedOnRollback = params.applyToRollback ?: false
	
	this.changeSet = changeSet
  }

  
  def prepend(Map params = [:]) {
    createSqlVisitor('prepend', params)
  }
  
  
  def append(Map params = [:]) {
    createSqlVisitor('append', params)
  }
  
  
  def replace(Map params = [:]) {
    createSqlVisitor('replace', params)
  }
  
  
  def regExpReplace(Map params = [:]) {
    createSqlVisitor('regExpReplace', params)
  }
  
  
  def createSqlVisitor(String type, Map params = [:]){	
	SqlVisitor sqlVisitor = SqlVisitorFactory.getInstance().create(type)
	  
	if(type == 'prepend' || type == 'append'){
	  if (params.value) {
		sqlVisitor.setValue(params.value)
	  } else {
		throw new ChangeLogParseException(changeSet.toString() + "  modifySql: Parameter 'value' not found")
	  }
	} 
	else {
      if (params.with && params.replace){
	    sqlVisitor.setReplace(params.replace)
	    sqlVisitor.setWith(params.with)
      } else {
	    throw new ChangeLogParseException(changeSet.toString() + "  modifySql: Parameters 'with' and/or 'replace' not found")
	  }
	}
	
	setProperties(sqlVisitor)
  }
  
	
  def setProperties(SqlVisitor sqlVisitor) {
	if(modifySqlDbmsList){
      sqlVisitor.setApplicableDbms(modifySqlDbmsList)
	}
	if(modifySqlContexts){
      sqlVisitor.setContexts(modifySqlContexts)
	}
	sqlVisitor.setApplyToRollback(modifySqlAppliedOnRollback)
	
	sqlVisitors << sqlVisitor
  }
}
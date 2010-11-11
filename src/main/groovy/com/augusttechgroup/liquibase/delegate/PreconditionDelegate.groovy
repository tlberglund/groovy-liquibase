//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

package com.augusttechgroup.liquibase.delegate

import liquibase.precondition.core.PreconditionContainer
import liquibase.precondition.core.PreconditionContainer.FailOption
import liquibase.precondition.core.PreconditionContainer.ErrorOption
import liquibase.precondition.core.PreconditionContainer.OnSqlOutputOption
import liquibase.precondition.core.DBMSPrecondition
import liquibase.precondition.core.RunningAsPrecondition
import liquibase.precondition.core.ChangeSetExecutedPrecondition
import liquibase.precondition.core.ColumnExistsPrecondition
import liquibase.precondition.core.TableExistsPrecondition
import liquibase.precondition.core.ViewExistsPrecondition
import liquibase.precondition.core.ForeignKeyExistsPrecondition
import liquibase.precondition.core.IndexExistsPrecondition
import liquibase.precondition.core.SequenceExistsPrecondition
import liquibase.precondition.core.PrimaryKeyExistsPrecondition
import liquibase.precondition.core.AndPrecondition
import liquibase.precondition.core.OrPrecondition

/**
 * <p></p>
 * 
 * @author Tim Berglund
 */
class PreconditionDelegate
{
  def preconditions
  final preconditionToClass = [
    dbms: [class: DBMSPrecondition, params: ['type']],
    runningAs: [class: RunningAsPrecondition, params: ['username']],
    changeSetExecuted: [class: ChangeSetExecutedPrecondition, params: ['id', 'author', 'changeLogFile']],
    columnExists: [class: ColumnExistsPrecondition, params: ['schemaName', 'columnName', 'columnName']],
    tableExists: [class: TableExistsPrecondition, params: ['schemaName', 'tableName']],
    viewExists: [class: ViewExistsPrecondition, params: ['schemaName', 'viewName']],
    foreignKeyConstraintExists: [class: ForeignKeyExistsPrecondition, params: ['schemaName', 'foreignKeyName']],
    indexExists: [class: IndexExistsPrecondition, params: ['schemaName', 'indexName']],
    sequenceExists: [class: SequenceExistsPrecondition, params: ['schemaName', 'sequenceName']],
    primaryKeyExists: [class: PrimaryKeyExistsPrecondition, params: ['schemaName', 'primaryKeyName', 'tableName']]
  ]

  
  PreconditionDelegate(Map params = [:]) {
    preconditions = new PreconditionContainer()

    if(params.onFail) {
      preconditions.onFail = FailOption."${params.onFail}"
    }

    if(params.onError) {
      preconditions.onError = ErrorOption."${params.onError}"
    }

    if(params.onUpdateSQL) {
      preconditions.onSqlOutput = OnSqlOutputOption."${params.onUpdateSQL}"
    }

    preconditions.onFailMessage = params.onFailMessage
    preconditions.onErrorMessage = params.onErrorMessage
  }


  //
  // Handles all non-nesting preconditions named in the preconditionToClass map.
  //
  void methodMissing(String name, args) {
    def preconditionData = preconditionToClass[name]
    if(preconditionData) {
      def precondition = preconditionData['class'].newInstance()
      def params = args[0]
      if(params != null && params instanceof Map) {
        args[0].each { key, value ->
          precondition[key] = value
        }
      }
      preconditions.addNestedPrecondition(precondition)
    }
  }


  def sqlCheck(Map params = [:], Closure closure) {

  }


  def customPrecondition(Map params = [:], Closure closure) {
    
  }

  
  def and(Closure closure) {
    def precondition = nestedPrecondition(AndPrecondition, closure)
    preconditions.addNestedPrecondition(precondition)
  }


  def or(Closure closure) {
    def precondition = nestedPrecondition(OrPrecondition, closure)
    preconditions.addNestedPrecondition(precondition)
  }


  private def nestedPrecondition(Class preconditionClass, Closure closure) {
    def delegate = new PreconditionDelegate()
    closure.delegate = delegate
    closure.resolveStrategy = Closure.DELEGATE_ONLY
    closure.call()

    def nestedPrecondition = preconditionClass.newInstance()
    delegate.preconditions.each { precondition ->
      nestedPrecondition.addNestedPrecondition(precondition)
    }

    return nestedPrecondition
  }
}
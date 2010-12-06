//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

package com.augusttechgroup.liquibase

import liquibase.serializer.ChangeLogSerializer
import liquibase.changelog.DatabaseChangeLog
import liquibase.changelog.ChangeSet
import liquibase.change.Change
import liquibase.sql.visitor.SqlVisitor
import liquibase.change.ColumnConfig
import liquibase.util.ISODateFormat
import liquibase.change.ConstraintsConfig

import java.sql.Timestamp


/**
 * An implementation
 *
 * @author Tim Berglund
 */
class GroovyChangeLogSerializer
  implements ChangeLogSerializer
{
  ISODateFormat isoFormat = new ISODateFormat()


  String[] getValidFileExtensions() {
    ['groovy']
  }


  String serialize(DatabaseChangeLog databaseChangeLog) {
    return null
  }


  String serialize(ChangeSet changeSet) {
    return null
  }


  String serialize(Change change) {
    return null
  }


  String serialize(SqlVisitor visitor) {
    return null
  }


  /*
    private ConstraintsConfig constraints;
   */
  String serialize(ColumnConfig columnConfig) {
    def propertyNames = [ 'name', 'type', 'value', 'valueNumeric', 'valueDate', 'valueBoolean', 'valueComputed', 'defaultValue', 'defaultValueNumeric', 'defaultValueDate', 'defaultValueBoolean', 'defaultValueComputed', 'autoIncrement', 'remarks' ]
    def properties = buildPropertyListFrom(propertyNames, columnConfig)
    def column = "column(${properties.join(', ')})"
    if(columnConfig.constraints) {
      """\
${column} {
  ${serialize(columnConfig.constraints)}
}"""
    }
    else {
      column
    }
  }


  String serialize(ConstraintsConfig constraintsConfig) {
    def propertyNames = [ 'nullable', 'primaryKey', 'primaryKeyName', 'primaryKeyTablespace', 'references', 'unique', 'uniqueConstraintName', 'check', 'deleteCascade', 'foreignKeyName', 'initiallyDeferred', 'deferrable' ]
    def properties = buildPropertyListFrom(propertyNames, constraintsConfig)
    "constraints(${properties.join(', ')})"
  }


  private buildPropertyListFrom(propertyNames, object) {
    def properties = []

    propertyNames.each { propertyName ->
      def propertyString
      def propertyValue = object[propertyName]
      if(propertyValue != null) {
        switch(propertyValue.class) {
          case Boolean:
            propertyString = Boolean.toString(propertyValue)
            break

          case Number:
            propertyString = propertyValue.toString()
            break

          case Timestamp:
            propertyString = "'${isoFormat.format((Timestamp)propertyValue)}'"
            break

          default:
            propertyString = "'${propertyValue.toString()}'"
            break
        }
        properties << "${propertyName}: ${propertyString}"
      }
    }

    return properties
  }

}

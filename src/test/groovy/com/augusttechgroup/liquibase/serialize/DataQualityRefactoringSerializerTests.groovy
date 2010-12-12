//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

package com.augusttechgroup.liquibase.serialize

import org.junit.Test
import static org.junit.Assert.*

import liquibase.change.core.DropDefaultValueChange
import liquibase.change.core.AddDefaultValueChange


/**
 * <p></p>
 * 
 * @author Tim Berglund
 */
class DataQualityRefactoringSerializerTests
  extends SerializerTests
{

  @Test
  void addDefaultValueBoolean() {
    def change = [
      tableName: 'monkey',
      schemaName: 'schema',
      columnName: 'emotion',
      defaultValueBoolean: true
    ] as AddDefaultValueChange

    def serializedText = serializer.serialize(change)
    def expectedText = "addDefaultValue(columnName: 'emotion', defaultValueBoolean: true, schemaName: 'schema', tableName: 'monkey')"
    assertEquals expectedText, serializedText
  }

}
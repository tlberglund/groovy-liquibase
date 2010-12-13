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
import liquibase.sql.visitor.ReplaceSqlVisitor

/**
 * <p></p>
 * 
 * @author Tim Berglund
 */
class SqlVisitorSerializerTests
  extends SerializerTests
{

  @Test
  void testReplaceSqlVisitorSerialize() {
    def visitor = [
       
    ] as ReplaceSqlVisitor
  }
}
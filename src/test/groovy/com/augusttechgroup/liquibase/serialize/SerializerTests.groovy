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

import liquibase.resource.FileSystemResourceAccessor
import liquibase.serializer.ChangeLogSerializerFactory

import org.junit.Before
import liquibase.serializer.ext.GroovyChangeLogSerializer
import java.text.SimpleDateFormat
import java.sql.Timestamp


/**
 * A base class providing support for test classes targeting serializations.
 *
 * @author Tim Berglund
 */
class SerializerTests
{
  def sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s")
  def resourceAccessor
  def serializerFactory
  def serializer

  @Before
  void registerSerializer() {
    resourceAccessor = new FileSystemResourceAccessor(baseDirectory: '.')
    serializerFactory = ChangeLogSerializerFactory.instance
    ChangeLogSerializerFactory.getInstance().register(new GroovyChangeLogSerializer())
    serializer = serializerFactory.getSerializer('groovy')
  }


  Timestamp parseSqlTimestamp(dateTimeString) {
    new Timestamp(sdf.parse(dateTimeString).time)
  }
}
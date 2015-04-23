/*
 * Copyright 2011-2014 Tim Berglund and Steven C. Saliman
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

package net.saliman.liquibase.serialize

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
class SerializerTests {
  def sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s")
  def resourceAccessor
  def serializerFactory
  def serializer

  @Before
  void registerSerializer() {
    resourceAccessor = new FileSystemResourceAccessor(baseDirectory: new File('.'))
    serializerFactory = ChangeLogSerializerFactory.instance
    ChangeLogSerializerFactory.getInstance().register(new GroovyChangeLogSerializer())
    serializer = serializerFactory.getSerializer('groovy')
  }


  Timestamp parseSqlTimestamp(dateTimeString) {
    new Timestamp(sdf.parse(dateTimeString).time)
  }
}


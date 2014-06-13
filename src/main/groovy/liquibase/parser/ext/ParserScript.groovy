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

package liquibase.parser.ext


abstract class ParserScript extends Script {

  @Override
  void setProperty(String name, value) {
    if ("databaseChangeLog" == name) {
      changeLogParser.processDatabaseChangeLogRootElement(changeLog, resourceAccessor, [value])
    }
    else {
      super.setProperty(name, value)
    }
  }

  @Override
  Object invokeMethod(String name, Object args) {
    if ("databaseChangeLog" == name) {
      changeLogParser.processDatabaseChangeLogRootElement(changeLog, resourceAccessor, args)
    }
    else {
      return super.invokeMethod(name, args)
    }
  }

}

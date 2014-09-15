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

databaseChangeLog(logicalFilePath: '.') {

  changeSet(author: 'tlberglund', id: 'change-set-001') {

      myParametrizedCustomChange{
          name('myProperty')
          value('prop1')
      }
      myParametrizedCustomChange{
          myProperty('prop2')
      }
      customChange(className: 'net.saliman.liquibase.custom.MyParametrizedCustomChange') {
          name('myProperty')
          value('prop3')
      }
      customChange(className: 'net.saliman.liquibase.custom.MyCustomSqlChange') {
      }
  }

    changeSet(author: 'tlberglund', id: 'change-set-001') {
        myParametrizedCustomChange{
            myProperty('prop4')
        }
    }

}

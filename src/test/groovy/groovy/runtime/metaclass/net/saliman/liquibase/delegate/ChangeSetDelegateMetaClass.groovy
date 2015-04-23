/*
 * Copyright 2011-2015 Tim Berglund and Steven C. Saliman
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
package groovy.runtime.metaclass.org.liquibase.groovy.delegate

import groovy.lang.ExpandoMetaClass
import groovy.lang.MetaClassRegistry

import org.codehaus.groovy.runtime.metaclass.ClosureMetaMethod.AnonymousMetaMethod

import org.liquibase.groovy.change.CustomProgrammaticChangeWrapper
import org.liquibase.groovy.custom.MyCustomSqlChange


/**
 * This defines an ExpandoMetaClass on org.liquibase.groovy.delegate.ChangeSetDelegate
 * so that we can manipulate the class at runtime to add new methods.  In this example,
 * we are adding the myCustomSqlChange which will add the MyCustomSqlChange and wrap it 
 * in the CustomProgrammaticChangeWrapper so it conforms to the liquibase API.
 * 
 * @author Jason Clawson
 */
class ChangeSetDelegateMetaClass extends ExpandoMetaClass {

  ChangeSetDelegateMetaClass(MetaClassRegistry reg, Class clazz) {
    super(clazz, true, false)

    addMetaMethod(new AnonymousMetaMethod({ addChange(new CustomProgrammaticChangeWrapper(new MyCustomSqlChange())) }, 
      'myCustomSqlChange', 
      clazz))

    initialize()
  }
}

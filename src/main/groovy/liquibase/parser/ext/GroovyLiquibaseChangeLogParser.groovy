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

import liquibase.parser.ChangeLogParser
import liquibase.changelog.DatabaseChangeLog
import liquibase.changelog.ChangeLogParameters
import liquibase.resource.ResourceAccessor
import liquibase.exception.ChangeLogParseException

import net.saliman.liquibase.delegate.DatabaseChangeLogDelegate
import org.codehaus.groovy.control.CompilerConfiguration


class GroovyLiquibaseChangeLogParser
  implements ChangeLogParser {


  DatabaseChangeLog parse(String physicalChangeLogLocation,
                          ChangeLogParameters changeLogParameters,
                          ResourceAccessor resourceAccessor) {

    physicalChangeLogLocation = physicalChangeLogLocation.replaceAll('\\\\', '/')
    physicalChangeLogLocation = physicalChangeLogLocation.replace(System.getProperty("user.dir").toString() + "/", "")
    def inputStream = resourceAccessor.getResourceAsStream(physicalChangeLogLocation)
    if(!inputStream) {
      throw new ChangeLogParseException(physicalChangeLogLocation + " does not exist")
    }

    try {
      def changeLog = new DatabaseChangeLog(physicalChangeLogLocation)
      changeLog.setChangeLogParameters(changeLogParameters)

      def binding = new Binding()
      def shell = new GroovyShell(binding)

      // Parse the script, give it the local changeLog instance, give it access
      // to root-level method delegates, and call.
      def script = shell.parse(new InputStreamReader(inputStream, "UTF8"))
      script.metaClass.getDatabaseChangeLog = { -> changeLog }
      script.metaClass.getResourceAccessor = { -> resourceAccessor }
      script.metaClass.methodMissing = changeLogMethodMissing
      script.run()

      // The changeLog will have been populated by the script
      return changeLog
    }
    finally {
      try {
        inputStream.close()
      }
      catch(Exception e) {
        // Can't do much more than hope for the best here
      }
    }
  }


  boolean supports(String changeLogFile, ResourceAccessor resourceAccessor) {
    changeLogFile.endsWith('.groovy')
  }


  int getPriority() {
    PRIORITY_DEFAULT
  }


  def getChangeLogMethodMissing() {
    { name, args ->
      if(name == 'databaseChangeLog') {
        processDatabaseChangeLogRootElement(databaseChangeLog, resourceAccessor, args)
      }
      else {
        throw new ChangeLogParseException("Unrecognized root element ${name}")
      }
    }
  }

  private def processDatabaseChangeLogRootElement(databaseChangeLog, resourceAccessor, args) {
    def delegate;
    def closure;
    
    switch(args.size()) {
      case 0:
        throw new ChangeLogParseException("databaseChangeLog element cannot be empty")
      
      case 1:
        closure = args[0]
        if(!(closure instanceof Closure)) {
          throw new ChangeLogParseException("databaseChangeLog element must be followed by a closure (databaseChangeLog { ... })")
        }
        delegate = new DatabaseChangeLogDelegate(databaseChangeLog)
        break
        
      case 2:
        def params = args[0]
        closure = args[1]
        if(!(params instanceof Map)) {
          throw new ChangeLogParseException("databaseChangeLog element must take parameters followed by a closure (databaseChangeLog(key: value) { ... })")
        }
        if(!(closure instanceof Closure)) {
          throw new ChangeLogParseException("databaseChangeLog element must take parameters followed by a closure (databaseChangeLog(key: value) { ... })")
        }
        delegate = new DatabaseChangeLogDelegate(params, databaseChangeLog)
        break
        
      default:
        throw new ChangeLogParseException("databaseChangeLog element has too many parameters: ${args}")
    }
    
    delegate.resourceAccessor = resourceAccessor
    closure.delegate = delegate
    closure.resolveStrategy = Closure.OWNER_FIRST
    closure.call()
  }
}


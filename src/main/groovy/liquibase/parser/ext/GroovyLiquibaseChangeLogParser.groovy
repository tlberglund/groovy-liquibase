//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

package liquibase.parser.ext

import liquibase.parser.ChangeLogParser
import liquibase.changelog.DatabaseChangeLog
import liquibase.changelog.ChangeLogParameters
import liquibase.resource.ResourceAccessor
import liquibase.exception.ChangeLogParseException

import com.augusttechgroup.liquibase.delegate.DatabaseChangeLogDelegate


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
      def script = shell.parse(inputStream)
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
    switch(args.size()) {
      case 0:
        throw new ChangeLogParseException("databaseChangeLog element cannot be empty")
      
      case 1:
        def closure = args[0]
        if(!(closure instanceof Closure)) {
          throw new ChangeLogParseException("databaseChangeLog element must be followed by a closure (databaseChangeLog { ... })")
        }
        def delegate = new DatabaseChangeLogDelegate(databaseChangeLog)
        delegate.resourceAccessor = resourceAccessor
        closure.delegate = delegate
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        break
        
      case 2:
        def params = args[0]
        def closure = args[1]
        if(!(params instanceof Map)) {
          throw new ChangeLogParseException("databaseChangeLog element must take parameters followed by a closure (databaseChangeLog(key: value) { ... })")
        }
        if(!(closure instanceof Closure)) {
          throw new ChangeLogParseException("databaseChangeLog element must take parameters followed by a closure (databaseChangeLog(key: value) { ... })")
        }
        def delegate = new DatabaseChangeLogDelegate(params, databaseChangeLog)
        closure.delegate = delegate
        closure.call()
        break
        
      default:
        throw new ChangeLogParseException("databaseChangeLog element has too many parameters: ${args}")
    }
  }
}


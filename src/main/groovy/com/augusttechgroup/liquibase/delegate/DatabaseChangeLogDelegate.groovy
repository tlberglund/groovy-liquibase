/*
 * Copyright 2011 Tim Berglund
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

package com.augusttechgroup.liquibase.delegate

import liquibase.changelog.ChangeSet
import liquibase.exception.ChangeLogParseException
import liquibase.parser.ChangeLogParserFactory
import liquibase.parser.core.xml.XMLChangeLogSAXHandler;


class DatabaseChangeLogDelegate {
  def databaseChangeLog
  def params
  def resourceAccessor


  DatabaseChangeLogDelegate(databaseChangeLog) {
    this([:], databaseChangeLog)
  }

  
  DatabaseChangeLogDelegate(Map params, databaseChangeLog) {
    this.params = params
    this.databaseChangeLog = databaseChangeLog
    params.each { key, value ->
      databaseChangeLog[key] = value
    }
  }
  
  
  void changeSet(Map params, closure) {
    def changeSet = new ChangeSet(
      params.id,
      params.author,
      params.alwaysRun?.toBoolean() ?: false,
      params.runOnChange?.toBoolean() ?: false,
      databaseChangeLog.filePath,
      params.context,
      params.dbms,
      params.runInTransaction?.toBoolean() ?: true)

    if(params.failOnError) {
      changeSet.failOnError = params.failOnError?.toBoolean()
    }

    if(params.onValidationFail) {
      changeSet.onValidationFail = ChangeSet.ValidationFailOption.valueOf(params.onValidationFail)
    }

    def delegate = new ChangeSetDelegate(changeSet: changeSet,
                                         databaseChangeLog: databaseChangeLog,
                                         resourceAccessor: resourceAccessor)
    closure.delegate = delegate
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure.call()
    
    databaseChangeLog.addChangeSet(changeSet)
  }


  void preConditions(Map params = [:], Closure closure) {
    databaseChangeLog.preconditions = PreconditionDelegate.buildPreconditionContainer(databaseChangeLog, params, closure)
  }


  void include(Map params = [:]) {
    def physicalChangeLogLocation = databaseChangeLog.physicalFilePath.replace(System.getProperty("user.dir").toString() + "/", "")
    def relativeToChangelogFile = false
    
    if (params.relativeToChangelogFile){
      relativeToChangelogFile = params.relativeToChangelogFile
    }
    
    if (params.file) {
      if (relativeToChangelogFile && (physicalChangeLogLocation.contains("/") || physicalChangeLogLocation.contains("\\\\"))){
        params.file = physicalChangeLogLocation.replaceFirst("/[^/]*\$","") + "/" + params.file
      }
      
      includeChangeLog(params.file)
    }
    else if (params.path) {
      if (!params.path.endsWith("/")) {
        params.path += "/"
      }
      
      if (relativeToChangelogFile && (physicalChangeLogLocation.contains("/") || physicalChangeLogLocation.contains("\\\\"))){
        params.path = physicalChangeLogLocation.replaceFirst("/[^/]*\$","") + "/" + params.path
      }
      
      def resources = resourceAccessor.getResources(params.path)
      resources.each { fileUrl ->
        if (!fileUrl.toExternalForm().startsWith("file:")) {
          def zipFileDir = XMLChangeLogSAXHandler.extractZipFile(fileUrl)
          fileUrl = new File(zipFileDir, params.path).toURI().toURL()
        }
        
        def file = new File(fileUrl.toURI());
        
        if (file.isDirectory()) {
          def names = []  
          file.eachFileMatch(~/.*.groovy/) { childFile ->
            names << childFile.name
          }
          names.sort().each { name ->
            includeChangeLog(params.path + name)
          }
        } else {
          includeChangeLog(params.path + file.name)
        }
      }
    }
  }


  private def includeChangeLog(filename) {
    def parser = ChangeLogParserFactory.getInstance().getParser(filename, resourceAccessor)
    def includedChangeLog = parser.parse(filename, databaseChangeLog.changeLogParameters, resourceAccessor)
    includedChangeLog?.changeSets.each { changeSet ->
      databaseChangeLog.addChangeSet(changeSet)
    }
    includedChangeLog?.preconditionContainer?.nestedPreconditions.each { precondition ->
      databaseChangeLog.preconditionContainer.addNestedPrecondition(precondition)
    }
  }


  void property(Map params = [:]) {
    def context = params['context'] ?: null
    def dbms = params['dbms'] ?: null
    def changeLogParameters = databaseChangeLog.changeLogParameters
    
    if (!params['file']) {
      changeLogParameters.set(params['name'], params['value'], context, dbms)
    } else {
      def props = new Properties()
      def propertiesStream = resourceAccessor.getResourceAsStream(params['file'])
      if (!propertiesStream) {
        throw new ChangeLogParseException("Unable to load file with properties: ${params['file']}")
      } else {
        props.load(propertiesStream)
        props.each { k, v ->
          changeLogParameters.set(k, v, context, dbms)
        }
      }
    }
  }
  
  def propertyMissing(String name) {
    def changeLogParameters = databaseChangeLog.changeLogParameters
    if (changeLogParameters.hasValue(name)) {
      return changeLogParameters.getValue(name)
    } else {
      throw new MissingPropertyException(name, this.class)
    }
  }

}

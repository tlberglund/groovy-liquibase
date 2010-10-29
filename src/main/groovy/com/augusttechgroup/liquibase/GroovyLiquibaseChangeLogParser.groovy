//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the GNU Lesser General Public License v2.1
//

package com.augusttechgroup.liquibase

import liquibase.parser.ChangeLogParser
import liquibase.changelog.DatabaseChangeLog
import liquibase.changelog.ChangeLogParameters
import liquibase.resource.ResourceAccessor


class GroovyLiquibaseChangeLogParser
  implements ChangeLogParser {


  DatabaseChangeLog parse(String physicalChangeLogLocation,
                          ChangeLogParameters changeLogParameters,
                          ResourceAccessor resourceAccessor) {
    def input = resourceAccessor.getResourceAsStream(physicalChangeLogLocation)

    def changeLog = new DatabaseChangeLog(physicalChangeLogLocation)
    databaseChangeLog.setChangeLogParameters(changeLogParameters);

    println input
  }


  boolean supports(String changeLogFile, ResourceAccessor resourceAccessor) {
    changeLogFile.endsWith('.groovy')
  }


  int getPriority() {
    PRIORITY_DEFAULT
  }

}
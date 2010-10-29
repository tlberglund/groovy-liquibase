package com.augusttechgroup.liquibase

import com.augusttechgroup.liquibase.GroovyLiquibaseChangeLogParser
import liquibase.parser.*
import liquibase.resource.FileSystemResourceAccessor


ChangeLogParserFactory.getInstance().register(new GroovyLiquibaseChangeLogParser())

changeLogFile = 'changelog.groovy'
parser = ChangeLogParserFactory.getInstance().getParser(changeLogFile, new FileSystemResourceAccessor(baseDirectory: '.'))
println parser

//.parse(changeLogFile, changeLogParameters, resourceAccessor);
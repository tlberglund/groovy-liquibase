# Groovy Liquibase
A pluggable parser for [Liquibase](http://liquibase.org) that allows the creation of changelogs in a Groovy DSL, rather than hurtful XML. If this DSL isn't reason enough to adopt Liquibase, then there is no hope for you. 

Presently a work in progress. The project aim is to create as close a match for the standard XML format as possible, such that migration back and forth is easy and very little new documentation should be necessary.

## How does it work?
To use groovy-liquibase you'll need gradle. [http://www.gradle.org/]
We use mysql on runtime in liquibase.gradle To use other dbms add your DBdriver on runtime.( not tested ).
After installing gradle you can use groovy-liquibase.

1)  Download the source and go into the directory

2)  command line $:  gradle build

3)  Create file database.properties

            #database.properties 

            url: "yourDatabaseUrl"
            username: "yourDatabaseUser"
            password: "yourPassword"
            change.log.file: changelog.groovy

4)  Create file changelog.groovy (See LiquibaseGroovyMigrations.pdf for content of this file)

5)  Execute liquibase functions: command line $:  

       gradle -q -b liquibase.gradle update
       gradle -q -b liquibase.gradle update -Dcount=1
       gradle -q -b liquibase.gradle rollback -Dtag="tag"
       gradle -q -b liquibase.gradle dbDoc -Ddir="directory"
       "" etc. ""



## Author
Work is currently being done by Tim Berglund of the [August Technology Group](http://augusttechgroup.com).

Fork is being worked on by Erwin van Brandwijk intern of [42bv Netherlands](http://www.42.nl).

## License
This code is released under the Apache Public License 2.0, just like Liquibase 2.0.

## TODOs

 * Support for the customChange. Using groovy code, liquibase changes and database SQL in a changeSet.
 * Support for the [property tag](http://www.liquibase.org/manual/changelog_parameters).
 * Support for the [modifySql tag](http://www.liquibase.org/manual/modify_sql?s[]=modifysql).
 * Support for extensions. modifyColumn is probably a good place to start.

 * Proper testing of validCheckSum under changeSet. It's implemented, but I have not tested it properly.
 * Integration testing. Everything of note is unit-tested so far, but there must be tests which mutate an actual database (in-memory HSQL would work fine) and make assertions on its final state.
 * Deployment to Maven Central as soon as it's baked enough to ask people to use it.

# Groovy Liquibase
A pluggable parser for [Liquibase](http://liquibase.org) that allows the creation of changelogs in a Groovy DSL, rather than hurtful XML. If this DSL isn't reason enough to adopt Liquibase, then there is no hope for you. 

Presently a work in progress. The project aim is to create as close a match for the standard XML format and the [Grails Database MigrationsPlugin](http://www.grails.org/plugin/database-migration) as possible, such that migration back and forth is easy and very little new documentation should be necessary.

## How does it work?
To use groovy-liquibase you'll need gradle. [http://www.gradle.org/]
We use mysql on runtime in liquibase.gradle To use other dbms add your DBdriver on runtime.( not tested ).
After installing gradle you can use groovy-liquibase.

1)  Download the source and go into the directory

2)  command line $:  gradle build

3)  Edit file database.properties

            #database.properties 

            url: "yourDatabaseUrl"
            username: "yourDatabaseUser"
            password: "yourPassword"
            change.log.file: changelog.groovy

4)  changelog.groovy calls al changelogs in /changelogs. Create file(s) changelog(s) in directory /changelogs (See LiquibaseGroovyMigrations.pdf for content of this file)

5)  Execute liquibase functions: command line $:  

       gradle -q -b liquibase.gradle "function"


## Functions


Standard Commands:

* update                         ---Updates database to current version
* updateSQL                      ---Writes SQL to update database to current version to STDOUT
* updateCount <num>              ---Applies next NUM changes to the database
* updateSQL <num>                ---Writes SQL to apply next NUM changes to the database
* rollback <tag>                 ---Rolls back the database to the the state is was when the tag was applied
* rollbackSQL <tag>              ---Writes SQL to roll back the database to that state it was in when the tag was applied to STDOUT
* rollbackToDate <date/time>     ---Rolls back the database to the the state is was at the given date/time. Date Format: yyyy-MM-dd HH:mm:ss
* rollbackToDateSQL <date/time>  ---Writes SQL to roll back the database to that state it was in at the given date/time to STDOUT
* rollbackCount <value>          ---Rolls back the last <value> change sets applied to the database
* rollbackCountSQL <value>       ---Writes SQL to roll back the last <value> change sets to STDOUT applied to the database
* futureRollbackSQL              ---Writes SQL to roll back the database to the current state after the changes in the changeslog have been applied
* updateTestingRollback          ---Updates database, then rolls back changes before updating again. Useful for testing rollback support
* generateChangeLog              ---Writes Change Log XML to copy the current state of the database to standard out

Diff Commands

* diff [diff parameters]          ---Writes description of differences to standard out
* diffChangeLog [diff parameters] ---Writes Change Log XML to update the database to the reference database to standard out

Documentation Commands

* dbDoc <outputDirectory>         ---Generates Javadoc-like documentation based on current database and change log

Maintenance Commands

* tag <tag string>          ---'Tags' the current database state for future rollback
* status 		    ---Outputs count of unrun changesets
* validate                  ---Checks changelog for errors
* clearCheckSums            ---Removes all saved checksums from database log. Useful for 'MD5Sum Check Failed' errors
* changelogSync             ---Mark all changes as executed in the database
* changelogSyncSQL          ---Writes SQL to mark all changes as executed in the database to STDOUT
* markNextChangeSetRan      ---Mark the next change changes as executed in the database
* markNextChangeSetRanSQL   ---Writes SQL to mark the next change as executed in the database to STDOUT
* listLocks                 ---Lists who currently has locks on the database changelog
* releaseLocks              ---Releases all locks on the database changelog
* dropAll                   ---Drop all database objects owned by user

## Author(s)
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

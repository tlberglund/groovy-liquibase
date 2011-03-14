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

       gradle -q -b liquibase.gradle "function"


## Functions
First list can also be executed with SQL, that will generate SQL in STDOUT. Example updateSQL.


* update		                - executes all changesets 
* update -Dcount=?	        - executes ? number of changesets
* rollback -Dtag=?	        - does a rollback to tag
* rollback -Dcount=?	        - does a rollback ? number of changesets
* rollback -Ddate=?	        - does a rollback to date (yyyy-MM-dd"T"hh:mm:ss)
* futureRollbackSQL	        - generates SQL to rollback the changesets that aren't executed
* changelogSync	        - set all changesets as executed in the database

* status		                - shows wich changesets have not been executed
* validate		                - checks if all changesets are correct
* listLocks		                - shows all locks on the database
* releaseLocks		        - delete all locks from the database
* clearChecksums	        - delete all md5 checksums, will be generated on next run
* markNextChangesetRan	- mark next changeset ran
* dropAll		                - delete all data objects from the current database

* diff -Durl=? -Dusername=? -Dpassword=?	- shows differences between current database and params database


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

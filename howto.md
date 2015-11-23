## Some hints on using the current state of the project
To use groovy-liquibase you'll need [Gradle](http://www.gradle.org/). 

1)  Download the source and go into the directory

2)  Command line $:  gradle build

3)  You have two options to set configuration:
    Database connection is also configured in liquibase.gradle on runtime (default mysql)

	1   Create file database.properties

            #database.properties 

            url: yourDatabaseUrl
            username: yourDatabaseUser
            password: yourPassword
            change.log.file: changelog.groovy
	    working.dir: path  (Optional)
	    classpath: driver.jar  (Optional)
	    driver: driverclass oracle.jdbc.OracleDriver  (Optional)

	2   Use system configurations at each command

	    gradle -q -b liquibase.gradle update \
		-Ddatabase.url=jdbc:mysql://localhost/test \
		-Ddatabase.username=root \
		-Ddatabase.password=admin \
		-Dchange.log.file=changelog.groovy \
		-Dworking.dir=changelog.groovy \  (Optional)
		-Ddatabase.driver=changelog.groovy     (Optional)
		-Ddatabase.classpath=changelog.groovy     (Optional)

4)  change.log.file is the master file where all the database changes are located.

5)  Execute liquibase functions: command line $:  

       gradle -q -b liquibase.gradle "function"


## Functions

###Standard Commands

* update                          ---Updates database to current version
* updateSQL                       ---Writes SQL to update database to current version to STDOUT
* update -Dliquibase.count=<num>            ---Applies next NUM changes to the database
* updateSQL -Dliquibase.count=<num>         ---Writes SQL to apply next NUM changes to the database
* rollback -Dliquibase.tag=<tag string>                  ---Rolls back the database to the the state is was when the tag was applied
* rollbackSQL -Dliquibase.tag=<tag string>               ---Writes SQL to roll back the database to that state it was in when the tag was applied to STDOUT
* rollback -Dliquibase.date=<date/time>     ---Rolls back the database to the the state is was at the given date/time. Date Format: yyyy-MM-dd HH:mm:ss
* rollbackSQL -Dliquibase.date=<date/time>  ---Writes SQL to roll back the database to that state it was in at the given date/time to STDOUT
* rollback -Dliquibase.count=<value>        ---Rolls back the last <value> change sets applied to the database
* rollbackSQL -Dliquibase.count<value>      ---Writes SQL to roll back the last <value> change sets to STDOUT applied to the database
* futureRollbackSQL               ---Writes SQL to roll back the database to the current state after the changes in the changeslog have been applied
* updateTestingRollback           ---Updates database, then rolls back changes before updating again. Useful for testing rollback support
* generateChangeLog               ---Writes Change Log XML to copy the current state of the database to standard out

###Diff Commands

* diff [diff parameters]          ---Writes description of differences to standard out [diff paramaters -Dliquibase.referenceUrl=, -Dliquibase.referenceUsername=, -Dliquibase.referencePassword=]

###Documentation Commands

* dbDoc -Dliquibase.doc.dir=<outputDirectory>         ---Generates Javadoc-like documentation based on current database and change log

###Maintenance Commands

* tag -Dliquibase.tag=<tag string>          ---'Tags' the current database state for future rollback
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

###Contexts

You can use contexts just as in liquibase core. Use -Dliquibase.contexts after any command. See [liquibase](http://www.liquibase.org/documentation/contexts.html) for full description .

###Running groovy-liquibase outside a specific directory

If you are using svn for your project and you don't want groovy-liquibase on it. 
You can use it from anywhere local on your machine and tell groovy-liquibase what the workingDirectory is.


This is usefull if you are working with svn and want different people to update the migrations. 
groovy-liquibase saves filename. That is his name and path from workingDir. 


How to use:  gradle -q -b liquibase.gradle update -Dworking.dir=/home/erwin/Desktop

Now it's executing all changelogs from path /home/erwin/Desktop.


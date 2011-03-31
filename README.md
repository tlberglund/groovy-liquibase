# Groovy Liquibase
A pluggable parser for [Liquibase](http://liquibase.org) that allows the creation of changelogs in a Groovy DSL, rather than hurtful XML. If this DSL isn't reason enough to adopt Liquibase, then there is no hope for you. 

Presently a work in progress. The project aim is to create as close a match for the standard XML format and the [Grails Database MigrationsPlugin](http://www.grails.org/plugin/database-migration) as possible, such that migration back and forth is easy and very little new documentation should be necessary.

A sample Gradle build file, liquibase.gradle, is provided as a convenience means of executing Liquibase commands using the Groovy DSL. It is not intended to be a long-term plugin solution.


## Author(s)
Work is currently being done by Tim Berglund of the [August Technology Group](http://augusttechgroup.com).

Additional contributions by Erwin van Brandwijk, intern of [42bv Netherlands](http://www.42.nl).

## License
This code is released under the Apache Public License 2.0, just like Liquibase 2.0.

## TODOs

 * Support for the customChange. Using groovy code, liquibase changes and database SQL in a changeSet.
 * Support for the [property tag](http://www.liquibase.org/manual/changelog_parameters).
 * Support for extensions. modifyColumn is probably a good place to start.

 * Proper testing of validCheckSum under changeSet. It's implemented, but I have not tested it properly.

 * Deployment to Maven Central as soon as it's baked enough to ask people to use it.

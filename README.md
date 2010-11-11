# Groovy Liquibase
A pluggable parser for [Liquibase](http://liquibase.org) that allows the creation of changelogs in a Groovy DSL, rather than hurtful XML. If this DSL isn't reason enough to adopt Liquibase, then there is no hope for you. 

Presently a work in progress. The project aim is to create as close a match for the standard XML format as possible, such that migration back and forth is easy and very little new documentation should be necessary.

## Author
Work is currently being done by Tim Berglund of the [August Technology Group](http://augusttechgroup.com).

## License
This code is released under the Apache Public License 2.0, just like Liquibase 2.0.

## TODOs

 * Support for the [property tag](http://www.liquibase.org/manual/changelog_parameters)
 * Support for the [rollback tag](http://www.liquibase.org/manual/rollback)
 * Support for the [includeall tag](http://www.liquibase.org/manual/includeall). This might be better implemented as an extension of include(), as in:
 
	include(path: '')
    
 * Support for extensions. modifyColumn is probably a good place to start.
 * Tests buildSimpleDateColumn, insertData, and updateData are timezone-sensitive. That should be fixed.
 * Proper testing of validCheckSum under changeSet. It's implemented, but I have not tested it properly.
 * Integration testing. Everything of note is unit-tested so far, but there must be tests which mutate an actual database (in-memory HSQL would work fine) and make assertions on its final state.
 * Deployment to Maven Central as soon as it's baked enough to ask people to use it.
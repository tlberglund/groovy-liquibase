# Groovy Liquibase
A pluggable parser for [Liquibase](http://liquibase.org) that allows the
creation of changelogs in a Groovy DSL, rather than hurtful XML. If this DSL
isn't reason enough to adopt Liquibase, then there is no hope for you.

## News
*Note:* Version 1.0.0 of this DSL uses Liquibase 3 instead of Liquibase 2.
Before upgrading, we strongly recommend the following procedure for upgrading:

 1. Make sure all databases are up to date using the older version of the
    DSL by running an update on all databases.

 2. Create a new, throw away database and use the new DSL to run all of yor
    change sets. on the new database.  This is because Liquibase 3 introduces
    some subtle differences in the way SQL is generated.  For example, adding a
    default value to a boolean column in MySql using ```defaultValue: "0"```
    worked fine in Liquibase 2, but in Liquibase 3, it generates invalid SQL.
    ```defaultValueNumeric: 0``` needs to be used instead.  This is also a good
    time to look for and fix any deprecation warnings.
 3. When you are sure all the change sets are correct for Liquibase 3, clear
    all checksums calculated by Liquibase 2 by using the ```clearChecksums```
    command in all databases.

 4. Finally, run a ```changeLogSync``` on all databases to calculate new
    checksums.

## Usage
The DSL syntax is intended to mirror the Liquibase XML syntax directly, such
that mapping elements and attributes from the Liquibase documentation to Groovy
builder syntax will result in a valid changelog. Hence this DSL is not
documented separately from the Liquibase XML format, except for the few minor
differences or enhancements to the XML format, and the one gaping hole in the
XML documentation.

- The documentation mentions a referencesUniqueColumn attribute of
  addForeignKeyConstraint, but it is ignored and marked deprecated, so we've
  deprecated it as well.
- createIndex and dropIndex have an undocumented attribute named "associatedWith".
  From an old Liquibase forum, it appears to be an attempt to solve the problem
  of some databases creating indexes on primary keys and foreign keys and
  constraints and others not.  The idea is that if a createIndex change is
  tagged with the reason, Liquibase can skip the creation of them if the
  database will inherently create one.  The Liquibase authors do say it is
  experimental, so use at your own risk...


- executeCommand has os, args can just be strings...
- loadData and loadUpdateData can use File as well as filename
- sql - comments must be BEFORE sql string.
- sql can just be a string.
- sqlFile says you can set the sql attribute, but it doesn't make sense, so
    we don't do it.
- stop 'message'  as well as xml stop(message: 'message')

## License
This code is released under the Apache Public License 2.0, just like Liquibase 2.0.

## TODOs

 * Support for the customChange. Using groovy code, liquibase changes and database SQL in a changeSet.
 * Support for the [property tag](http://www.liquibase.org/manual/changelog_parameters).
 * Support for extensions. modifyColumn is probably a good place to start.
 * Proper testing of validCheckSum under changeSet. It's implemented, but I have not tested it properly.
 * Support for comments in a sql change.
 * At least a warning when we ignore an attribute that is not legal for a change.

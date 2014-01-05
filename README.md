# Groovy Liquibase
A pluggable parser for [Liquibase](http://liquibase.org) that allows the
creation of changelogs in a Groovy DSL, rather than hurtful XML. If this DSL
isn't reason enough to adopt Liquibase, then there is no hope for you.

## News
**IMPORTANT NOTE FOR USERS UPGRADING FROM A PRE 1.0.0 RELEASE OF THE GROOVY DSL:**

Version 1.0.0 of the Groovy Liquibase DSL uses Liquibase 3, instead of Liquibase
2, and several things have been deprecated from the Groovy DSL to maintain
compatibility with Liquibase XML. A list of deprecated items can be found in the *Usage* section.  To upgrade to version 1.0.0, we strongly recommend the following procedure:

1. Make sure all of your Liquibase managed databases are up to date by running
   an update on them *before upgrading the Groovy DSL*.

2. Create a new, throw away database and test your Liquibase change sets by
   running an update on this new database with the latest version of the Groovy
   DSL.  This is important because of the deprecated items in the Groovy DSL,
   and because there are some subtle differences in the ways the Liquibase versions generate SQL.  For example, adding a default value to a boolean column in MySql using ```defaultValue: "0"``` worked fine in Liquibase 2, but in Liquibase 3, it generates SQL that doesn't work for MySql;
    ```defaultValueNumeric: 0``` needs to be used instead.  Here is a tip to help find deprecation warnings: redirect stderr to a file.  Most of what Liquibase tells you is on stderr, but not deprecation warnings.

3. Once you are sure all of your change sets work with the latest Groovy DSL and Liquibase 3, clear
    all checksums that were calculated by Liquibase 2 by using the ```clearChecksums```
    command in all databases.

4. Finally, run a ```changeLogSync``` on all databases to calculate new
    checksums.

## Usage
The DSL syntax is intended to mirror the Liquibase XML syntax directly, such
that mapping elements and attributes from the Liquibase documentation to Groovy
builder syntax will result in a valid changelog. Hence this DSL is not
documented separately from the Liquibase XML format.  We will, however let you know about the minor differences or enhancements to the XML format, and help out with a couple of the gaping holes in Liquibase's documentation of the XML.

##### Deprecated Items
* In the Liquibase XML, you can set a ```sql``` attrobute in a ```sqlFile```
  change, but that doesn't make a lot sense, so this has been disabled in the
  Groovy DSL.
* The documentation mentions a ```referencesUniqueColumn``` attribute of the
  ```addForeignKeyConstraint``` change, but what it doesn't tell you is that it
  is ignored.  In the code, Liquibase has marked this item as being deprecated,
  so we've deprecated it as well.

##### Additions to the XML format:
* The Groovy DSL supports a simplified means of passing arguments to the
  ```exececuteCommand change```.  Instead of:

  ```groovy
execute {
  arg(value: 'somevalue')
}
```
  You can use this the simpler form:
```groovy
execute {
  arg 'somevalue'
}
```

* The ```sql``` change does not require a closure for the actual SQL.  You can
  just pass the string like this: ```sql 'select some_stuff from some_table'```
  If you want to use the ```comments``` element of a ```sql``` change, you need
  to use the closure form, and the comment must be in the closure BEFORE the
  SQL, like this:

  ```groovy
sql {
  comment('we should not have deleted this...')
  'delete from my_table'
}
```
* The  ```stop``` change can take a message as an argument as well as an
  attribute.  In other words, ```stop 'message'``` works as well as the more
  XMLish ```xml stop(message: 'message')```
* A ```customPrecondition```  can take parameters.  the XMLish way to pass them
  is with ```param(name: 'myParam', value: 'myValue')``` statements in the
  customPrecondition's closure.  In the Groovy DSL, you can also have
   ```myParam('myValue')```
* The ```validChecksum``` element of a change set is not well documented.
  Basically you can use this when changeSet's current checksum will not match
  what is stored in the database. This might happen if you, for example want to
  reformat a changeSet to add white space.  This doesn't change the
  functionality of the changeset, but it will cause Liquibase to generate new
  checksums for it.  The ```validateChecksum``` element tells Liquibase to
  consider the checksums in the ```validChecksum``` element to be valid, even
  if it doesn't match what is in the database.
* The Liquibase documentation tells you how to set a property for a
  databaseChangeLog by using the ```property``` element.  What it doesn't tell
  you is that you can also set properties by loading a property file.  To do
  this, you can have ```property(file: 'my_file.properties')``` in the closure
  for the databaseChangeLog.
* Liquibase has an ```includeAll``` element in the databaseChangeLog that
  includes all the files in the given directory.  The Groovy DSL implementation
  only includes groovy files, and it makes sure they are included in
  alphabetical order.  This is really handy for keeping changes in a different
  file for each release.  As long as the file names are named with the release
  numbers in mind, Liquibase will apply changes in the correct order.
* Remember, the Groovy DSL is basically just Groovy closures, so you can use
  groovy code to do things you could never do in XML, such as this:

```groovy
sql { """
  insert into some_table(data_column, date_inserted)
  values('some_data', '${new Date().toString()}')
"""
}
```

##### Items that were left out of the XML documentation
* The ```createIndex``` and ```dropIndex``` changes have an undocumented
  ```associatedWith``` attribute.  From an old Liquibase forum, it appears to be
   an attempt to solve the problem that occurs because some databases
   automatically create indexes on primary keys and foreign keys, and others
   don't.  The idea is that you would have a change to create the primary key or
   foreign key, and another to create the index for it.  The index change would
   use the ```associatedWith``` attribute to let Liquibase know that this index
   will already exist for some databases so that Liquibase can skip the change
   if we are in one of those databases.  The Liquibase authors do say it is
   experimental, so use at your own risk...
* The ```executeCommand``` change has an undocumented ```os``` attribute.  The
  ```os``` attribute is a string with  a list of operating systems under which
  the command should execute.  If present, the ```os.name``` system property
  will be checked against this list, and the command will only run if the
  operating system is in the list.
- column has some undocumented attributes that we should document here...
- constraints have some undocumented attributes that we should document here...

## License
This code is released under the Apache Public License 2.0, just like Liquibase 2.0.

## TODOs

 * Support for the customChange. Using groovy code, liquibase changes and database SQL in a changeSet.
 * Support for extensions. modifyColumn is probably a good place to start.

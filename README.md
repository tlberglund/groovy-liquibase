# Groovy Liquibase
A pluggable parser for [Liquibase](http://liquibase.org) that allows the creation of changelogs in a Groovy DSL, rather than hurtful XML. If this DSL isn't reason enough to adopt Liquibase, then there is no hope for you.

The DSL syntax is intended to mirror the Liquibase XML syntax directly, such that mapping elements and attributes from the Liquibase documentation to Groovy builder syntax will result in a valid changelog. Hence this DSL is not documented separately from the Liquibase XML format.

## Author(s)
Work is currently being done by [Tim Berglund])https://github.com/tlberglund).

Additional contributions by Erwin van Brandwijk, intern of [42bv Netherlands](http://www.42.nl) and many other faithful OSS supporters who have sent pull requests.

## License
This code is released under the Apache Public License 2.0, just like Liquibase 2.0.

## TODOs

 * Support for the customChange. Using groovy code, liquibase changes and database SQL in a changeSet.
 * Support for the [property tag](http://www.liquibase.org/manual/changelog_parameters).
 * Support for extensions. modifyColumn is probably a good place to start.
 * Proper testing of validCheckSum under changeSet. It's implemented, but I have not tested it properly.

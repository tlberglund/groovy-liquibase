/*
 * Copyright 2011-2014 Tim Berglund and Steven C. Saliman
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.saliman.liquibase.delegate

import liquibase.exception.ChangeLogParseException

/**
 * This class processes the {@code arg} closure that can be present in an
 * {@code executeCommand} change.  The arguments that are processed by this
 * closure will not be expanded for databaseChangeLog property substitution.
 * That is up to the caller.
 *
 * @author Steven C. Saliman
 */
class ArgumentDelegate {
	def args = []
	def changeSetId = '<unknown>' // used for error messages
	def changeName = '<unknown>' // used for error messages

	/**
	 * Process an argument where the argument is simply a string.  This is not
	 * how the Liquibase XML works, but it is really nice shorthand.
	 * @param value the argument to add
	 */
	def arg(String value) {
		args << value
	}

	/**
	 * Process an argument where the argument is in the {@code value} entry of
	 * the given map.  This is consistent with how Liquibase XML works.
	 * @param valueMap the map containing the argument.
	 */
	def arg(Map valueMap) {
		// we want a helpful message if the value map has anything other than a
		// "value" key.
		valueMap.each { key, value ->
			if ( key == "value") {
				args << valueMap.value
			} else {
				throw new ChangeLogParseException("ChangeSet '${changeSetId}': '${key}' is not a valid argument atrribute of ${changeName} changes")
			}
		}
	}

	/**
	 * Groovy calls methodMissing when it can't find a matching method to call.
	 * We use it to tell the user which changeSet had the invalid element.
	 * @param name the name of the method Groovy wanted to call.
	 * @param args the original arguments to that method.
	 */
	def methodMissing(String name, args) {
		throw new ChangeLogParseException("ChangeSet '${changeSetId}': '${name}' is not a valid child element of ${changeName} changes")
	}

}


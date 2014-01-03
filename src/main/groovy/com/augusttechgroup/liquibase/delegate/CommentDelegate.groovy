/*
 * Copyright 2011 Tim Berglund
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

package com.augusttechgroup.liquibase.delegate

/**
 * This class processes the closure that can be present in a {@code sql} change.
 * the closure will either contain just SQL, or a comment and some SQL.
 * For now, we only support the SQL coming after the comment.
 */
class CommentDelegate {
	String comment = null
	def changeSetId = '<unknown>' // used for error messages
	def changeName = '<unknown>' // used for error messages

	/**
	 * Process a comment in the closure
	 * @param value the value of the comment.
	 */
	void comment(String value) {
		if ( comment != null ) {
			comment = "${comment} ${value}"
		} else {
		  this.comment = value
		}
	}

	/**
	 * Groovy calls methodMissing when it can't find a matching method to call.
	 * We use it to tell the user which changeSet had the invalid element.
	 * @param name the name of the method Groovy wanted to call.
	 * @param args the original arguments to that method.
	 */
	def methodMissing(String name, args) {
		throw new IllegalArgumentException("ChangeSet '${changeSetId}': '${name}' is not a valid child element of ${changeName} changes")
	}

}
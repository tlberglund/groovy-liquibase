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

import liquibase.change.ColumnConfig
import liquibase.exception.ChangeLogParseException
import liquibase.util.ObjectUtil;

/**
 * This class is a delegate for nested columns found frequently in the DSL, such
 * as inside the {@code createTable} change.  It can handle both normal columns,
 * as found in the {@code createTable} change, and the
 * {@code LoadDataColumnConfig} columns that can be found in the {@code loadData}
 * change.  When the {@link ChangeSetDelegate} creates a ColumnDelegate for a,
 * given change, it will need to set the correct columnConfigClass.
 * <p>
 * This class also handles the nested where clause that appears in the
 * {@code update} and {@code delete} changes.  This probably does not cohere
 * with the overall purpose of the class, but it is much better than having to
 * duplicate the column processing logic since the {@code update} change uses
 * columns and a where clause.
 */
class ColumnDelegate {
	def columns = []
	def columnConfigClass = ColumnConfig
	def whereClause
	def databaseChangeLog
	def changeSetId = '<unknown>' // used for error messages
	def changeName = '<unknown>' // used for error messages

	/**
	 * Parse a single column entry ina clpsure.
	 * @param params the attributes to set.
	 * @param closure a child closure to call, such as a constraint clause
	 */
	def column(Map params, Closure closure = null) {
		def column = columnConfigClass.newInstance()

		params.each { key, value ->
			try {
			  ObjectUtil.setProperty(column, key, expandExpressions(value))
			} catch(RuntimeException e) {
				// Rethrow as an ChangeLogParseException with a more helpful message
				// than you'll get from the Liquibase helper.
				throw new ChangeLogParseException("ChangeSet '${changeSetId}': '${key}' is not a valid column attribute for '${changeName}' changes.")
			}
		}

		if ( closure ) {
			def constraintDelegate = new ConstraintDelegate(databaseChangeLog: databaseChangeLog,
			                                                changeSetId: changeSetId,
			                                                changeName: changeName)
			closure.delegate = constraintDelegate
			closure.resolveStrategy = Closure.DELEGATE_FIRST
			closure.call()
			column.constraints = constraintDelegate.constraint
		}

		columns << column
	}

	/**
	 * Set up a where clause for the closure.
	 * @param whereClause the where clause to use.
	 */
	def where(String whereClause) {
		this.whereClause = expandExpressions(whereClause)
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

	private def expandExpressions(expression) {
		databaseChangeLog.changeLogParameters.expandExpressions(expression.toString())
	}

}
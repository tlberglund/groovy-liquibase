/*
 * Copyright 2011-2015 Tim Berglund and Steven C. Saliman
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

package org.liquibase.groovy.delegate

/**
 * Little utility with helper methods that all the delegates can use.
 *
 * @author Steven C. Saliman
 */
class DelegateUtil {
	/**
	 * Helper method that expands a text expression, replacing variables inside
	 * strings with their values from the database change log parameters.
	 * @param expression the text to expand
	 * @param databaseChangeLog the database change log
	 * @return the text, after substitutions have been made.
	 */
	static def expandExpressions(expression, databaseChangeLog) {
		// Don't expand a null into the text "null"
		if ( expression != null ) {
			databaseChangeLog.changeLogParameters.expandExpressions(expression.toString())
		}
	}
}

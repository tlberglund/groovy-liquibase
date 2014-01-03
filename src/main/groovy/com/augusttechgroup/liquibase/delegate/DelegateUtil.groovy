package com.augusttechgroup.liquibase.delegate

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

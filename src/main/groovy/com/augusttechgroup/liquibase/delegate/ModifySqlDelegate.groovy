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

import liquibase.Contexts
import liquibase.exception.ChangeLogParseException
import liquibase.sql.visitor.SqlVisitor;
import liquibase.sql.visitor.SqlVisitorFactory;
import liquibase.changelog.ChangeSet
import liquibase.util.ObjectUtil

/**
 * This delegate handles the Liquibase ModifySql element, which can be used
 * to tweak the SQL that Liquibase generates.
 *
 * @author Tim Berglund
 * @author Steven C. Saliman
 */
class ModifySqlDelegate {
	def modifySqlDbmsList
	def modifySqlAppliedOnRollback
	def modifySqlContexts
	def sqlVisitors = []
	def changeSet


	ModifySqlDelegate(Map params = [:], ChangeSet changeSet) {
		this.changeSet = changeSet

		// params are optional
		if ( params != null ) {
			def unsupportedKeys = params.keySet() - ['dbms', 'context', 'applyToRollback']
			if ( unsupportedKeys.size() > 0 ) {
				throw new ChangeLogParseException("ChangeSet '${changeSet.id}':  '${unsupportedKeys.toArray()[0]}' is not a supported attribute of the 'modifySql' element.")
			}

			if ( params.dbms ) {
				// Expand expressions, then split into a list.
				def value = DelegateUtil.expandExpressions(params.dbms, changeSet.changeLog)
				modifySqlDbmsList = value.replaceAll(" ", "").split(',') as Set
			}
			if ( params.context ) {
				// expand expressions, then split into a list.
				def value = DelegateUtil.expandExpressions(params.context, changeSet.changeLog)
				modifySqlContexts = value.replaceAll(" ", "").split(',') as Set
			}

			modifySqlAppliedOnRollback = false
			if ( params.applyToRollback ) {
				modifySqlAppliedOnRollback = params.applyToRollback.toBoolean()
			}
		}
	}


	def prepend(Map params = [:]) {
		createSqlVisitor('prepend', params)
	}


	def append(Map params = [:]) {
		createSqlVisitor('append', params)
	}


	def replace(Map params = [:]) {
		createSqlVisitor('replace', params)
	}


	def regExpReplace(Map params = [:]) {
		createSqlVisitor('regExpReplace', params)
	}


	private def createSqlVisitor(String type, Map params = [:]) {
		SqlVisitor sqlVisitor = SqlVisitorFactory.getInstance().create(type)

		// Pass parameters through to the underlying Liquibase object.
		params.each { key, value ->
			try {
				ObjectUtil.setProperty(sqlVisitor, key, DelegateUtil.expandExpressions(value, changeSet.changeLog))
			} catch (RuntimeException e) {
				// Rethrow as an ChangeLogParseException with a more helpful message
				// than you'll get from the Liquibase helper.
				throw new ChangeLogParseException("ChangeSet '${changeSet.id}': '${key}' is not a valid attribute for '${type}' motifySql elements.", e)
			}
		}

		if ( modifySqlDbmsList ) {
			sqlVisitor.setApplicableDbms(modifySqlDbmsList)
		}
		if ( modifySqlContexts ) {
			sqlVisitor.setContexts(new Contexts(modifySqlContexts))
		}
		sqlVisitor.setApplyToRollback(modifySqlAppliedOnRollback)

		sqlVisitors << sqlVisitor
	}

	/**
	 * Groovy calls methodMissing when it can't find a matching method to call.
	 * We use it to tell the user which changeSet had the invalid element.
	 * @param name the name of the method Groovy wanted to call.
	 * @param args the original arguments to that method.
	 */
	def methodMissing(String name, params) {
		throw new ChangeLogParseException("ChangeSet '${changeSet.id}': '${name}' is not a valid child element of modifySql closures.")
	}

}
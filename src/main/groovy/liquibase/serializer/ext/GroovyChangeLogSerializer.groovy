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

package liquibase.serializer.ext

import liquibase.serializer.ChangeLogSerializer
import liquibase.changelog.ChangeSet
import liquibase.change.Change
import liquibase.serializer.LiquibaseSerializable
import liquibase.sql.visitor.SqlVisitor
import liquibase.change.ColumnConfig
import liquibase.util.ISODateFormat
import liquibase.change.ConstraintsConfig
import java.sql.Timestamp


/**
 *  This class is the main Groovy DSL serializer.  It creates Groovy changelogs
 *  for liquibase.  It must be in the liquibase.serializer.ext package to be
 *  found by Liquibase at runtime.
 *
 * @author Tim Berglund
 * @author Steven C. Saliman
 */
class GroovyChangeLogSerializer
				implements ChangeLogSerializer {
	ISODateFormat isoFormat = new ISODateFormat()

	/**
	 * What file extensions can this serializer handle?
	 * @return an array of valid file extensions.
	 */
	@Override
	String[] getValidFileExtensions() {
		['groovy']
	}

	/**
	 * Convert a single serializable Liquibase change into its Groovy
	 * representation.
	 * @param change the change to serialize.
	 * @param pretty whether or not to make it pretty.  It doesn't matter what
	 *        you pass here because this DSL refuses to make a serialization that
	 *        isn't pretty.
	 * @return the Groovy representation of the change.
	 */
	@Override
	String serialize(LiquibaseSerializable change, boolean pretty) {
		// call the appropriate helper through the magic of polymorphism.
		serializeObject(change)

	}

//	@Override
	@Override
	void write(List changeSets, OutputStream out) {
		out << 'databaseChangeLog {\n'
		out << changeSets.collect { changeSet -> indent(serialize(changeSet, true)) }.join('\n\n')
		out << '\n\n}\n'
	}


	@Override
	void append(ChangeSet changeSet, File changeLogFile) throws IOException {
		throw new UnsupportedOperationException("""GroovyChangeLogSerializer does not append changelog content.
  To append a newly generated changelog to an existing changelog, specify a new filename
  for the new changelog, then copy and paste that content into the existing file.""")
	}

	//---------------------------------------------------------------------------
	// In Liquibase 2.x, there were many different serialize methods for different
	// types of liquibase objects. In version 3.0, the different serializable
	// objects all implement a common interface, and the serialize methods were
	// replaced by a single method.  However, we want to do different things with
	// different objects with respect to ordering of attributes, etc. Therefore,
	// certain objects have their own serialize methods which will be called by
	// the master method.  These methods are private to force outside classes to
	// use serialization method defined by the interface.

	private String serializeObject(ChangeSet changeSet) {
		def attrNames = ['id', 'author', 'runAlways', 'runOnChange', 'failOnError', 'context', 'dbms']
		def attributes = [
						id    : changeSet.id,
						author: changeSet.author
		]
		def children = []

		//
		// Do these the hard way to keep them out of the map if they're false
		//

		if ( changeSet.isAlwaysRun() ) {
			attributes.runAlways = true
		}

		if ( changeSet.isRunOnChange() ) {
			attributes.runOnChange = true
		}

		if ( changeSet.failOnError ) {
			attributes.failOnError = changeSet.failOnError?.toString()
		}

		if ( changeSet.contexts && changeSet.contexts.contexts ) {
			attributes.context = changeSet.getContexts().getContexts().join(',')
		}

		if ( changeSet.dbmsSet ) {
			attributes.dbms = changeSet.dbmsSet.join(',')
		}

		if ( changeSet.comments?.trim() ) {
			children << "comment \"${changeSet.comments.replaceAll('"', '\\\"')}\""
		}

		changeSet.changes.each { change -> children << serialize(change, true) }

		def renderedChildren = children.collect { child -> indent(child) }.join('\n')
		return """\
changeSet(${buildPropertyListFrom(attrNames, attributes).join(', ')}) {
${renderedChildren}
}""".toString()
	}


	private String serializeObject(Change change) {
		def fields = change.serializableFields
		def children = []
		def attributes = []
		def textBody
		fields.each { field ->
			def fieldName = field
			def fieldValue = change.getSerializableFieldValue(fieldName)

			// TODO: The TextNode annotation used to mark a special case where
			// we set the text body to the field value.  This only applied to
			// UpdateDataChange and DeleteDataChange.  we need to figure out what to
			// do with these classes in 3.0
//	    def textNodeAnnotation = fieldValue.class.getAnnotation(StringBuilder.class)
			def textNodeAnnotation = null
			if ( textNodeAnnotation ) {
				textBody = fieldValue
			} else if ( fieldValue instanceof Collection ) {
				fieldValue.findAll { it instanceof ColumnConfig }.each {
					children << serialize(it, true)
				}
			} else if ( fieldValue instanceof ColumnConfig ) {
				children << serialize(fieldValue, true)
			} else if ( fieldName in ['procedureBody', 'sql', 'selectQuery'] ) {
				textBody = fieldValue
			} else {
				attributes << fieldName
			}
		}

		attributes = attributes.sort { it }

		def serializedChange
		if ( attributes ) {
			serializedChange = "${change.serializedObjectName}(${buildPropertyListFrom(attributes, change).join(', ')})"
		} else {
			serializedChange = "${change.serializedObjectName}"
		}

		if ( children ) {
			def renderedChildren = children.collect { child -> indent(child) }.join('\n')
			serializedChange = """\
${serializedChange} {
${renderedChildren}
}"""
		} else if ( textBody ) {
			serializedChange = """\
${serializedChange} {
  "${textBody}"
}"""
		}

		return serializedChange
	}


	private String serializeObject(ColumnConfig columnConfig) {
		def propertyNames = ['name', 'type', 'value', 'valueNumeric', 'valueDate', 'valueBoolean', 'valueComputed', 'defaultValue', 'defaultValueNumeric', 'defaultValueDate', 'defaultValueBoolean', 'defaultValueComputed', 'autoIncrement', 'remarks']
		def properties = buildPropertyListFrom(propertyNames, columnConfig)
		def column = "column(${properties.join(', ')})"
		if ( columnConfig.constraints ) {
			"""\
${column} {
  ${serialize(columnConfig.constraints, true)}
}"""
		} else {
			column
		}
	}


	private String serializeObject(ConstraintsConfig constraintsConfig) {
		def propertyNames = ['nullable', 'primaryKey', 'primaryKeyName', 'primaryKeyTablespace', 'references', 'referencedTableName', 'referencedColumnNames', 'unique', 'uniqueConstraintName', 'checkConstraint', 'deleteCascade', 'foreignKeyName', 'initiallyDeferred', 'deferrable']
		"constraints(${buildPropertyListFrom(propertyNames, constraintsConfig).join(', ')})"
	}


	private String serializeObject(SqlVisitor visitor) {
		"${visitor.name}(${buildPropertyListFrom(visitor.getSerializableFields(), visitor).join(', ')})"
	}

	private String serializeObject(LiquibaseSerializable change) {
		def fields = change.getSerializableFields()
		def children = []
		def attributes = []
		def textBody = null
		fields.each { field ->
			def fieldValue = change.getSerializableFieldValue(field)
			if ( fieldValue == null ) {
				return
			}
			def serializationType = change.getSerializableFieldType(field)

			// TODO: The TextNode annotation used to mark a special case where
			// we set the text body to the field value.  This only applied to
			// UpdateDataChange and DeleteDataChange.  we need to figure out what to
			// do with these classes in 3.0
			def textNodeAnnotation = fieldValue.class.getAnnotation(Integer.class)
			if ( textNodeAnnotation ) {
				textBody = fieldValue
			} else if ( fieldValue instanceof Collection ) {
				fieldValue.findAll { it instanceof LiquibaseSerializable }.each {
					children << serialize(it, true)
				}
//      } else if (fieldValue instanceof Map) {
//			  for (Map.Entry entry : (Set<Map.Entry>) ((Map) fieldValue).entrySet()) {
//			  	children << setValueOnNode((String) entry.getKey(), entry.getValue(), serializationType);
//        }
			} else if ( field in ['procedureBody', 'sql', 'selectQuery'] ) {
				textBody = fieldValue
			} else if ( fieldValue instanceof ChangeSet ) {
				children << serialize(fieldValue, true)
			} else if ( fieldValue instanceof LiquibaseSerializable ) {
				children << serialize((LiquibaseSerializable) fieldValue, true)
			} else if ( serializationType.equals(LiquibaseSerializable.SerializationType.NESTED_OBJECT) ) {
				children << serialize(fieldValue, true);
			} else if ( serializationType.equals(LiquibaseSerializable.SerializationType.DIRECT_VALUE) ) {
				textBody = fieldValue.toString()
			} else {
				attributes << field
			}
		}

		attributes = attributes.sort { it }

		def serializedChange
		if ( attributes ) {
			serializedChange = "${change.serializedObjectName}(${buildPropertyListFrom(attributes, change).join(', ')})"
		} else {
			serializedChange = "${change.serializedObjectName}"
		}

		if ( children ) {
			def renderedChildren = children.collect { child -> indent(child) }.join('\n')
			serializedChange = """\
${serializedChange} {
${renderedChildren}
}"""
		} else if ( textBody ) {
			serializedChange = """\
${serializedChange} {
  "${textBody}"
}"""
		}

		return serializedChange
	}

	/**
	 * Indents lines of text by two spaces.
	 * @param text the text to indent
	 * @return the indented text
	 */
	private indent(text) {
		text?.readLines().collect { line -> "  ${line}" }.join('\n')
	}

	/**
	 * Builds the correct string representation of an object's properties based
	 * on the property's type.
	 * @param propertyNames the names of the properties we're interested in.
	 * @param object an array of strings representing each property.
	 * @return
	 */
	private buildPropertyListFrom(propertyNames, object) {
		def properties = []

		propertyNames.each { propertyName ->
			def propertyString
			def propertyValue = object[propertyName]
			if ( propertyValue != null ) {
				switch (propertyValue.class) {
					case Boolean:
						propertyString = Boolean.toString(propertyValue)
						break

					case BigInteger:
					case BigDecimal:
					case Number:
						propertyString = propertyValue.toString()
						break

					case Timestamp:
						propertyString = "'${isoFormat.format((Timestamp) propertyValue)}'"
						break

					default:
						if ( propertyValue ) {
							propertyString = "'${propertyValue.toString()}'"
						}
						break
				}
				if ( propertyString ) {
					properties << "${propertyName}: ${propertyString}"
				}
			}
		}

		return properties
	}

}

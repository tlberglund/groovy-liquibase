package groovy.runtime.metaclass.com.augusttechgroup.liquibase.delegate

import groovy.lang.ExpandoMetaClass
import groovy.lang.MetaClassRegistry

import org.codehaus.groovy.runtime.metaclass.ClosureMetaMethod.AnonymousMetaMethod

import com.augusttechgroup.liquibase.change.CustomProgrammaticChangeWrapper
import com.augusttechgroup.liquibase.custom.MyCustomSqlChange

/**
 * This defines an ExpandoMetaClass on com.augusttechgroup.liquibase.delegate.ChangeSetDelegate
 * so that we can manipulate the class at runtime to add new methods.  In this example,
 * we are adding the myCustomSqlChange which will add the MyCustomSqlChange and wrap it 
 * in the CustomProgrammaticChangeWrapper so it conforms to the liquibase API.
 * 
 * 
 * @author Jason Clawson
 */
class ChangeSetDelegateMetaClass  extends ExpandoMetaClass {
	ChangeSetDelegateMetaClass(MetaClassRegistry reg, Class clazz) {
		super(clazz, true, false);
		
		addMetaMethod(new AnonymousMetaMethod({
			addChange(new CustomProgrammaticChangeWrapper(new MyCustomSqlChange()));
		}, "myCustomSqlChange", clazz));
		
		initialize();
	}
}

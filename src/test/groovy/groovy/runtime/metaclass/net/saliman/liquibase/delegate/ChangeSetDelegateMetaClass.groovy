package groovy.runtime.metaclass.net.saliman.liquibase.delegate

import org.codehaus.groovy.runtime.metaclass.ClosureMetaMethod.AnonymousMetaMethod

/**
 * This defines an ExpandoMetaClass on net.saliman.liquibase.delegate.ChangeSetDelegate
 * so that we can manipulate the class at runtime to add new methods.  In this example,
 * we are adding the myCustomSqlChange which will add the MyCustomSqlChange and wrap it 
 * in the CustomProgrammaticChangeWrapper so it conforms to the liquibase API.
 * 
 * @author Jason Clawson
 */
class ChangeSetDelegateMetaClass
  extends ExpandoMetaClass {

  ChangeSetDelegateMetaClass(MetaClassRegistry reg, Class clazz) {
    super(clazz, true, false)

    addMetaMethod(new AnonymousMetaMethod(
          {
              customChange([className: 'net.saliman.liquibase.custom.MyCustomSqlChange'], {})
          },
      'myCustomSqlChange',
      clazz))


    addMetaMethod(new AnonymousMetaMethod(
         {Closure closure = null ->
                customChange([className: 'net.saliman.liquibase.custom.MyParametrizedCustomChange'], closure)
         },
         'myParametrizedCustomChange',
         clazz))

    initialize()
  }
}

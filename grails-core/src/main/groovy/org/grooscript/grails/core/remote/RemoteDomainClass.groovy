package org.grooscript.grails.core.remote

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass(['org.grooscript.grails.core.remote.RemoteDomainClassImpl'])
public @interface RemoteDomainClass {
}

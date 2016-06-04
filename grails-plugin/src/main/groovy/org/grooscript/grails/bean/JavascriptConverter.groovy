package org.grooscript.grails.bean

import grails.plugin.cache.Cacheable
import grails.util.GrailsNameUtils
import org.grooscript.grails.core.Conversion
import org.grooscript.grails.core.GrailsConversions

import javax.annotation.ParametersAreNonnullByDefault

import static grails.util.Environment.isDevelopmentEnvironmentAvailable
import static org.grooscript.grails.util.Util.*
import static org.grooscript.grails.core.Conversion.COMPONENT_EXTENSION
import static org.grooscript.grails.core.Conversion.REMOTE_DOMAIN_EXTENSION

@ParametersAreNonnullByDefault
class JavascriptConverter {

    private boolean sourceCodeAvailable = developmentEnvironmentAvailable
    private Conversion grailsConversions = new GrailsConversions()

    @Cacheable('conversions')
    String toJavascript(String groovyCode, Map conversionOptions = null) {
        if (!groovyCode)
            ''

        try {
            grailsConversions.convertToJavascript(groovyCode, conversionOptions ?: [:])
        } catch (e) {
            consoleError "Error converting to javascript: ${e.message}"
            ''
        }
    }

    String convertRemoteDomainClass(String domainClassName) {

        String result
        if (sourceCodeAvailable) {
            result = grailsConversions.convertRemoteDomainClass(domainClassName)
        } else {
            result = getResourceText(GrailsNameUtils.getShortName(domainClassName) + REMOTE_DOMAIN_EXTENSION)
        }
        result
    }

    String getComponentCodeConverted(String fullClassName, String shortClassName, String nameComponent) {
        String result
        if (sourceCodeAvailable) {
            result = grailsConversions.convertComponent(fullClassName, nameComponent)
        } else {
            result = getResourceText(shortClassName + COMPONENT_EXTENSION)
        }
        result
    }
}

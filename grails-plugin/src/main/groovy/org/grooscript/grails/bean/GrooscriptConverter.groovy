package org.grooscript.grails.bean

import grails.plugin.cache.Cacheable
import org.grooscript.GrooScript
import org.grooscript.grails.core.Conversion
import org.grooscript.grails.core.GrailsConversions
import org.grooscript.grails.remote.RemoteDomainClass

import javax.annotation.ParametersAreNonnullByDefault

import static grails.util.Environment.isDevelopmentEnvironmentAvailable
import static org.grooscript.grails.util.Util.*

/**
 * @author Jorge Franco <jorge.franco@osoco.es>
 */
@ParametersAreNonnullByDefault
class GrooscriptConverter {

    private static final String COMPONENT_EXTENSION = '.cs'
    private static final List DEFAULT_CONVERSION_SCOPE_VARS = ['$', 'gsEvents', 'window',
                                                               'document', 'HtmlBuilder',
                                                               'GQueryImpl', 'Observable',
                                                               'ClientEventHandler',
                                                               'GrooscriptGrails']

    private boolean sourceCodeAvailable = developmentEnvironmentAvailable
    private Conversion grailsConversions = new GrailsConversions()

    @Cacheable('conversions')
    String toJavascript(String groovyCode, Map conversionOptions = null) {
        if (!groovyCode)
            ''

        try {
            GrooScript.convert(groovyCode, addDefaultOptions(conversionOptions ?: [:]))
        } catch (e) {
            consoleError "Error converting to javascript: ${e.message}"
            ''
        }
    }

    String convertRemoteDomainClass(String domainClassName) {
        String domainFileText = getDomainFileText(domainClassName)
        if (!domainFileText) {
            consoleWarning "Domain class file ${domainClassName} not found!"
            ''
        }

        try {
            GrooScript.convert(domainFileText, customizationAstOption(RemoteDomainClass))
        } catch (e) {
            consoleError "Error converting domain class file ${domainClassName}: ${e.message}"
            ''
        }
    }

    String getComponentCodeConverted(String fullClassName, String shortClassName, String nameComponent) {
        String result
        if (sourceCodeAvailable) {
            String source = getClassSource(fullClassName)
            result = grailsConversions.convertComponent(source, shortClassName, nameComponent)
        } else {
            result = getResourceText(shortClassName + COMPONENT_EXTENSION)
        }
        result
    }

    private static Map addDefaultOptions(Map options) {
        if (!options.classpath)
            options.classpath = []
        else {
            if (options.classpath instanceof String)
                options.classpath = [options.classpath]
        }

        if (!options.classpath.contains(GROOVY_SRC_DIR))
            options.classpath << GROOVY_SRC_DIR

        addScopeVars(options)
    }

    private static Map addScopeVars(Map options) {
        if (!options.mainContextScope)
            options.mainContextScope = []

        options.mainContextScope.addAll DEFAULT_CONVERSION_SCOPE_VARS
        options
    }


}

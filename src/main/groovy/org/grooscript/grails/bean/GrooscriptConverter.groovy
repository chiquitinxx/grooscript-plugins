package org.grooscript.grails.bean

import grails.plugin.cache.Cacheable
import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import org.grooscript.grails.component.Component
import org.grooscript.grails.remote.RemoteDomainClass

import static org.grooscript.grails.util.Util.*

/**
 * User: jorgefrancoleza
 * Date: 22/09/13
 */
class GrooscriptConverter {

    static final DEFAULT_CONVERSION_SCOPE_VARS = ['$', 'gsEvents', 'window', 'document', 'HtmlBuilder',
              'GQueryImpl', 'Observable', 'ClientEventHandler', 'GrooscriptGrails']

    @Cacheable('conversions')
    String toJavascript(String groovyCode, Map conversionOptions = null) {
        String jsCode = ''
        if (groovyCode) {
            try {
                conversionOptions = addDefaultOptions(conversionOptions)
                jsCode = GrooScript.convert(groovyCode, conversionOptions)
            } catch (e) {
                consoleError "Error converting to javascript: ${e.message}"
            }
        }
        jsCode
    }

    String convertRemoteDomainClass(String domainClassName) {
        String result = null
        try {
            String domainFileText = getDomainFileText(domainClassName)
            if (domainFileText) {
                try {
                    result = GrooScript.convert(domainFileText, customizationAstOption(RemoteDomainClass))
                } catch (e) {
                    consoleError "Error converting domain class file ${domainClassName}: ${e.message}"
                }
            } else {
                consoleWarning "Domain file not found ${domainClassName}"
            }
        } catch (e) {
            consoleError "Exception converting domain class (${domainClassName}) file: ${e.message}"
        }
        result
    }

    String convertComponent(String groovyCode) {
        Map conversionOptions = [:]
        conversionOptions[ConversionOptions.CLASSPATH.text] = GROOVY_SRC_DIR
        conversionOptions[ConversionOptions.INCLUDE_DEPENDENCIES.text] = true
        conversionOptions[ConversionOptions.CUSTOMIZATION.text] = {
            ast(Component)
        }
        toJavascript(groovyCode, addScopeVars(conversionOptions))
    }

    private Map addDefaultOptions(Map options) {
        options = options ?: [:]
        options = addGroovySourceClassPathIfNeeded(options)
        options = addScopeVars(options)
        options
    }

    private Map addScopeVars(Map options) {
        if (!options.mainContextScope) {
            options.mainContextScope = []
        }
        options.mainContextScope.addAll DEFAULT_CONVERSION_SCOPE_VARS
        options
    }

    private Map addGroovySourceClassPathIfNeeded(Map options) {
        if (!options.classpath) {
            options.classpath = []
        } else {
            if (options.classpath instanceof String) {
                options.classpath = [options.classpath]
            }
        }
        if (!options.classpath.contains(GROOVY_SRC_DIR)) {
            options.classpath << GROOVY_SRC_DIR
        }
        options
    }
}

package org.grooscript.grails.bean

import grails.plugin.cache.Cacheable
import org.grooscript.GrooScript
import org.grooscript.grails.remote.RemoteDomainClass

import static org.grooscript.grails.util.Util.*

/**
 * User: jorgefrancoleza
 * Date: 22/09/13
 */
class GrooscriptConverter {

    static final DEFAULT_CONVERSION_SCOPE_VARS = ['$', 'gsEvents', 'window', 'document']

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

    private addDefaultOptions(options) {
        options = options ?: [:]
        options = addGroovySourceClassPathIfNeeded(options)
        options = addScopeVars(options)
        options
    }

    private addScopeVars(options) {
        if (!options.mainContextScope) {
            options.mainContextScope = []
        }
        options.mainContextScope.addAll DEFAULT_CONVERSION_SCOPE_VARS
        options
    }

    private addGroovySourceClassPathIfNeeded(options) {
        if (!options.classPath) {
            options.classPath = []
        } else {
            if (options.classPath instanceof String) {
                options.classPath = [options.classPath]
            }
        }
        if (!options.classPath.contains(GROOVY_SRC_DIR)) {
            options.classPath << GROOVY_SRC_DIR
        }
        options
    }
}

package org.grooscript.grails.bean

import grails.plugin.cache.Cacheable
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder
import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import org.grooscript.grails.component.Component
import org.grooscript.grails.remote.RemoteDomainClass

import javax.annotation.ParametersAreNonnullByDefault

import static org.grooscript.grails.util.Util.*

/**
 * @author Jorge Franco <jorge.franco@osoco.es>
 */
@ParametersAreNonnullByDefault
class GrooscriptConverter {

    private static final List DEFAULT_CONVERSION_SCOPE_VARS = ['$', 'gsEvents', 'window',
                                                               'document', 'HtmlBuilder',
                                                               'GQueryImpl', 'Observable',
                                                               'ClientEventHandler',
                                                               'GrooscriptGrails']

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

    String convertComponent(String groovyCode) {
        Map conversionOptions = [:]
        conversionOptions[ConversionOptions.CLASSPATH.text] = GROOVY_SRC_DIR
        conversionOptions[ConversionOptions.INCLUDE_DEPENDENCIES.text] = true
        conversionOptions[ConversionOptions.CUSTOMIZATION.text] = {
            CompilerCustomizationBuilder.withConfig(new CompilerConfiguration()) {
                ast(Component)
            }
        }
        toJavascript(groovyCode, addScopeVars(conversionOptions))
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

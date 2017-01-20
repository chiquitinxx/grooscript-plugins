package org.grooscript.grails.core.converter

import org.codehaus.groovy.control.CompilerConfiguration
import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import org.grooscript.grails.core.component.Component

import static org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder.withConfig

class CoreConverter implements Converter {

    String convert(String groovyCode, Map conversionOptions) {
        GrooScript.convert(groovyCode, conversionOptions)
    }

    String convertComponent(String groovyCode, Map conversionOptions) {
        conversionOptions[ConversionOptions.CUSTOMIZATION.text] = {
            withConfig(new CompilerConfiguration()) {
                ast(Component)
            }
        }
        GrooScript.convert(groovyCode, conversionOptions)
    }
}

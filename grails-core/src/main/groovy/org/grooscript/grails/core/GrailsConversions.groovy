package org.grooscript.grails.core

import org.grooscript.convert.ConversionOptions
import org.grooscript.grails.core.converter.Converter

class GrailsConversions implements Conversion {

    private static final String SEP = System.getProperty('file.separator')
    private static final String DOMAIN_DIR = "grails-app${SEP}domain"
    private static final String GROOVY_SRC_DIR = "src${SEP}main${SEP}groovy"
    private static final List DEFAULT_CONVERSION_SCOPE_VARS = [
            '$', 'gsEvents', 'window', 'document', 'HtmlBuilder',
            'GQueryImpl', 'Observable', 'ClientEventHandler', 'GrooscriptGrails']

    private Converter converter = new Converter()

    @Override
    String convertComponent(File file) {
        Map conversionOptions = [:]
        conversionOptions[ConversionOptions.CLASSPATH.text] = GROOVY_SRC_DIR
        conversionOptions[ConversionOptions.INCLUDE_DEPENDENCIES.text] = true
        converter.convertComponent(file.text, addScopeVars(conversionOptions))
    }

    private static Map addScopeVars(Map options) {
        if (!options.mainContextScope)
            options.mainContextScope = []

        options.mainContextScope.addAll DEFAULT_CONVERSION_SCOPE_VARS
        options
    }
}

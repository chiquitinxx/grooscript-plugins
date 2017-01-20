package org.grooscript.grails.core.converter

interface Converter {

    String convert(String groovyCode, Map conversionOptions)
    String convertComponent(String groovyCode, Map conversionOptions)
}

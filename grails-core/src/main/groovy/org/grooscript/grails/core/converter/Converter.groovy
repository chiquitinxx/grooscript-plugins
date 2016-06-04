package org.grooscript.grails.core.converter

interface Converter {

    String convertComponent(String groovyCode, Map conversionOptions)
}

package org.grooscript.grails.core

interface Conversion {
    String convertComponent(String componentGroovyCode, String className, String nameComponent)
}
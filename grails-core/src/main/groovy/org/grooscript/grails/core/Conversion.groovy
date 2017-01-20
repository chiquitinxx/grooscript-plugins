package org.grooscript.grails.core

interface Conversion {

    static final String COMPONENT_EXTENSION = '.gcs'

    void setBaseDir(String path)
    String convertToJavascript(String groovyCode, Map conversionOptions)
    String convertComponent(String remoteComponentClassFullName, String nameComponent)
    List saveConversionForPackaging(File path, String fileName, String jsCode)
}
package org.grooscript.grails.core

interface Conversion {

    static final String COMPONENT_EXTENSION = '.gcs'
    static final String REMOTE_DOMAIN_EXTENSION = '.grs'

    void setBaseDir(String path)
    String convertToJavascript(String groovyCode, Map conversionOptions)
    String convertComponent(String remoteComponentClassFullName, String nameComponent)
    String convertRemoteDomainClass(String domainClassFullName)
    List saveConversionForPackaging(File path, String fileName, String jsCode)
}
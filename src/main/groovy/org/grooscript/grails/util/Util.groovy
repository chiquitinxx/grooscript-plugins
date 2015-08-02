package org.grooscript.grails.util

import org.grooscript.grails.tag.GrooscriptTagLib

/**
 * User: jorgefrancoleza
 * Date: 13/09/13
 */
class Util {

    static final SEP = System.getProperty('file.separator')
    static final String GROOVY_SRC_DIR = "src${SEP}main${SEP}groovy"
    static final String DOMAIN_DIR = "grails-app${SEP}domain"

    static final PLUGIN_MESSAGE = '[Grooscript Plugin]'

    static void consoleMessage(message) {
        println "${PLUGIN_MESSAGE} [INFO] $message"
    }

    static void consoleError(message) {
        println "\u001B[91m${PLUGIN_MESSAGE} [ERROR] $message\u001B[0m"
    }

    static void consoleWarning(message) {
        println "\u001B[93m${PLUGIN_MESSAGE} [WARNING] $message\u001B[0m"
    }

    static String getNewTemplateName() {
        'fTemplate' + new Date().time.toString()
    }

    static String getDomainFileText(String domainClassCanonicalName) {
        def filePath = "${DOMAIN_DIR}${SEP}${getFileNameFromDomainClassCanonicalName(domainClassCanonicalName)}"
        def file = new File(filePath)
        if (file && file.exists()) {
            return file.text
        } else {
            consoleError("Fail find domain class file: ${filePath}")
            return null
        }
    }

    static Map customizationAstOption(Class clazz) {
        [customization: {
            ast(clazz)
        }]
    }

    static String getResourceText(String filePath) {
        GrooscriptTagLib.classLoader.getResource(filePath).text
    }

    private static getFileNameFromDomainClassCanonicalName(String domainClassCanonicalName) {
        "${domainClassCanonicalName.replaceAll(/\./, SEP)}.groovy"
    }
}

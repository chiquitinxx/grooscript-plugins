package org.grooscript.grails.util

import org.grooscript.GrooScript

/**
 * User: jorgefrancoleza
 * Date: 13/09/13
 */
class Util {

    static final SEP = System.getProperty('file.separator')
    static final String GROOVY_SRC_DIR = "src${SEP}main${SEP}groovy"
    static final String DOMAIN_DIR = "grails-app${SEP}domain"

    static final PLUGIN_MESSAGE = '[Grooscript Plugin]'

    static consoleMessage(message) {
        println "${PLUGIN_MESSAGE} [INFO] $message"
    }

    static consoleError(message) {
        println "\u001B[91m${PLUGIN_MESSAGE} [ERROR] $message\u001B[0m"
    }

    static consoleWarning(message) {
        println "\u001B[93m${PLUGIN_MESSAGE} [WARNING] $message\u001B[0m"
    }

    static String getNewTemplateName() {
        'fTemplate' + new Date().time.toString()
    }

    static String getDomainFileText(String domainClassCanonicalName) {
        def file = new File("${DOMAIN_DIR}${SEP}${getFileNameFromDomainclassCanonicalName(domainClassCanonicalName)}")
        file && file.exists() ? file.text : null
    }

    static addCustomizationAstOption(Class clazz) {
        GrooScript.setConversionProperty('customization', {
            ast(clazz)
        })
    }

    private static getFileNameFromDomainclassCanonicalName(String domainClassCanonicalName) {
        "${domainClassCanonicalName.replaceAll(/\./, SEP)}.groovy"
    }
}

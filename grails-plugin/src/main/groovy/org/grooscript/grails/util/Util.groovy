package org.grooscript.grails.util

import org.grooscript.grails.tag.GrooscriptTagLib
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
final class Util {

    private Util() {
        // don't init me
    }

    private static final String PLUGIN_MESSAGE = '[Grooscript Plugin]'

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
        'fTemplate' + String.valueOf(new Date().time)
    }

    static String getResourceText(String shortClassName) {
        GrooscriptTagLib.classLoader.getResource(shortClassName).text
    }

    static String removeLastSemicolon(String code) {
        return code.lastIndexOf(';') >= 0 ? code.substring(0, code.lastIndexOf(';')) : code
    }
}

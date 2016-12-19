/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.grails.util

import org.grooscript.grails.tag.GrooscriptTagLib
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
final class Util {

    private Util() {
        // don't init me
    }

    private static final String PLUGIN_MESSAGE = '[Grooscript Grails Plugin]'

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

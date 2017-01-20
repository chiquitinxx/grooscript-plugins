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
package org.grooscript.grails.bean

import grails.plugin.cache.Cacheable
import org.grooscript.grails.core.Conversion
import org.grooscript.grails.core.GrailsConversions

import javax.annotation.ParametersAreNonnullByDefault

import static grails.util.Environment.isDevelopmentEnvironmentAvailable
import static org.grooscript.grails.util.Util.*
import static org.grooscript.grails.core.Conversion.COMPONENT_EXTENSION

@ParametersAreNonnullByDefault
class JavascriptConverter {

    private boolean sourceCodeAvailable = developmentEnvironmentAvailable
    private Conversion grailsConversions = new GrailsConversions()

    @Cacheable('conversions')
    String toJavascript(String groovyCode, Map conversionOptions = null) {
        if (!groovyCode)
            ''

        try {
            grailsConversions.convertToJavascript(groovyCode, conversionOptions ?: [:])
        } catch (e) {
            consoleError "Error converting to javascript: ${e.message}"
            ''
        }
    }

    String getComponentCodeConverted(String fullClassName, String shortClassName, String nameComponent) {
        String result
        if (sourceCodeAvailable) {
            result = grailsConversions.convertComponent(fullClassName, nameComponent)
        } else {
            result = getResourceText(shortClassName + COMPONENT_EXTENSION)
        }
        result
    }
}

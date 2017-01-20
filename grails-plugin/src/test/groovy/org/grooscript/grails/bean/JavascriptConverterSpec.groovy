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

import org.grooscript.grails.core.Conversion
import org.grooscript.grails.util.Util
import spock.lang.Specification

class JavascriptConverterSpec extends Specification {

    def 'convert to javascript without conversion options'() {
        given:
        conversion.convertToJavascript(groovyCode, [:]) >> jsCode

        when:
        def result = javascriptConverter.toJavascript(groovyCode, null)

        then:
        result == jsCode
    }

    def 'get component converted code with source code available'() {
        given:
        javascriptConverter.sourceCodeAvailable = true
        conversion.convertComponent(fullClassName, nameComponent) >> jsCode

        when:
        String result = javascriptConverter.getComponentCodeConverted(fullClassName, shortClassName, nameComponent)

        then:
        result == jsCode
    }

    def 'get component converted code without source code available'() {
        given:
        GroovySpy(Util, global: true)
        javascriptConverter.sourceCodeAvailable = false

        when:
        String result = javascriptConverter.getComponentCodeConverted(fullClassName, shortClassName, nameComponent)

        then:
        1 * Util.getResourceText(shortClassName + '.gcs') >> jsCode
        result == jsCode
    }

    private final String fullClassName = 'fullClassName'
    private final String shortClassName = 'shortClassName'
    private final String nameComponent = 'nameComponent'
    private final String groovyCode = 'any groovy code'
    private final String jsCode = 'any js code'
    private Conversion conversion = Stub(Conversion)
    private JavascriptConverter javascriptConverter = new JavascriptConverter()

    def setup() {
        javascriptConverter.grailsConversions = conversion
    }
}

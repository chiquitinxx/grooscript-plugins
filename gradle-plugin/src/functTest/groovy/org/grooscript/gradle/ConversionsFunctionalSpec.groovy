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
package org.grooscript.gradle

import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Unroll
class ConversionsFunctionalSpec extends AbstractFunctionalSpec {

    void "convert files"() {
        given:
        copyTestResourcesFiles('C.groovy', 'UseC.groovy', 'E.groovy')
        buildFile << """
grooscript {
    source = ['${fileToConvert}.groovy']
    destination = '.'
    classpath = ['.']
}"""

        when:
        def result = runWithArguments('convert')
        def generatedFile = new File(testProjectDir.root.absolutePath + SEP + fileToConvert + '.js')

        then:
        result.task(":convert").outcome == SUCCESS

        and:
        generatedFile.exists()

        where:
        fileToConvert << ['C', 'UseC', 'E']
    }
}

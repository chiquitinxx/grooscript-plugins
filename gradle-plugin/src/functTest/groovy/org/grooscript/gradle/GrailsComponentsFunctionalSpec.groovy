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

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class GrailsComponentsFunctionalSpec extends GrailsFunctionalSpec {

    void 'download grails project and build it'() {
        given:
        File grailsDir = new File(grailsProjectDir)
        File buildFile = new File(grailsProjectDir + SEP + 'build.gradle')

        expect:
        grailsDir.isDirectory()
        buildFile.exists()
        buildFile.text.concat('id \'org.grooscript.conversion\'')

        when:
        def result = runWithArguments('build')

        then:
        result.task(":build").outcome == SUCCESS
    }
}

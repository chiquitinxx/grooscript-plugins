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

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import spock.lang.Specification

class RequireJsTaskSpec extends Specification {

    static final SOURCE = 'source'
    static final DESTINATION = 'destination'
    Project project
    RequireJsTask task

    def setup() {
        project = ProjectBuilder.builder().build()

        task = project.task('require', type: RequireJsTask)
        task.project = project
        task.sourceFile = SOURCE
        task.destinationFolder = DESTINATION
    }

    def 'create the task'() {
        expect:
        task instanceof RequireJsTask
    }

    def 'classpath is mandatory'() {
        when:
        task.convertRequireJs()

        then:
        thrown(GradleException)
    }

    def 'do default conversion'() {
        given:
        GroovySpy(GrooScript, global: true)
        project.extensions.requireJs = [classpath: ['src/main/groovy']]

        when:
        task.convertRequireJs()

        then:
        1 * GrooScript.convertRequireJs(project.file(SOURCE).path, project.file(DESTINATION).path, [
                classpath: [project.file('src/main/groovy').path],
                initialText: null,
                finalText: null,
                mainContextScope: null,
                customization: null,
                nashornConsole: false
        ]) >> null
        0 * _
    }
}

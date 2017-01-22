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
import spock.lang.Specification
import spock.lang.Unroll

class ChangesTaskSpec extends Specification {

    Project project
    ChangesTask task

    def setup() {
        project = ProjectBuilder.builder().build()

        task = project.task('spyChanges', type: ChangesTask)
    }

    def 'create the task'() {
        expect:
        task instanceof ChangesTask
    }

    @Unroll
    def 'run the task without files or onChange throws error'() {
        when:
        task.files = files
        task.onChanges = actions
        task.detectChanges()

        then:
        thrown(GradleException)

        where:
        files   | actions
        ['one'] | null
        null    | null
        null    | { -> }
    }

    def 'run the task with correct data'() {
        given:
        project.extensions.modifications = [:]

        when:
        task.files = ['file']
        task.onChanges = { it -> println it }
        task.detectChanges()

        then:
        notThrown(GradleException)
    }
}

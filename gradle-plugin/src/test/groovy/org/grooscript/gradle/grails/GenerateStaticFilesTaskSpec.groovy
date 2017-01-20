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
package org.grooscript.gradle.grails

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Ignore
import spock.lang.Specification

class GenerateStaticFilesTaskSpec extends Specification {

    @Ignore
    void 'start task'() {
        expect:
        task.generateGrooscriptStaticFiles()
    }

    Project project
    GenerateStaticFilesTask task

    def setup() {
        project = ProjectBuilder.builder().build()

        task = project.task('generateGrailsFiles', type: GenerateStaticFilesTask)
        task.project = project
    }
}
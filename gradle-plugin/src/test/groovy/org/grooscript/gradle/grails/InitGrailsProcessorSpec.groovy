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
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import spock.lang.Specification

class InitGrailsProcessorSpec extends Specification {

    void 'start grails processor in a not a grails project'() {
        when:
        initGrailsProcessor.start(projectWithoutGrailsFolder)

        then:
        0 * assetCompileTask._
    }

    void 'start grails processor in a grails project'() {
        when:
        initGrailsProcessor.start(projectWithGrails)

        then:
        1 * assetCompileTask.dependsOn(generateStaticFilesTask)
    }

    private Task assetCompileTask = Mock(Task)
    private Task generateStaticFilesTask = Stub(Task)
    private TaskContainer taskContainer = Stub(TaskContainer) {
        it.getByName('assetCompile') >> assetCompileTask
        it.getByName('generateGrailsFiles') >> generateStaticFilesTask
    }
    private File grailsAppFolder = new File('.')
    private Project projectWithoutGrailsFolder = Stub(Project) {
        it.file('grails-app/views') >> null
    }
    private Project projectWithGrails = Stub(Project) {
        it.file('grails-app/views') >> grailsAppFolder
        it.tasks >> taskContainer
    }
    private InitGrailsProcessor initGrailsProcessor = new InitGrailsProcessor()
}
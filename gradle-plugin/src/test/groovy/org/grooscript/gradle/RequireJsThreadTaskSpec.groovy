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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.grooscript.gradle.require.RequireJsActor
import spock.lang.Specification

class RequireJsThreadTaskSpec extends Specification {

    def 'create the task'() {
        expect:
        task instanceof RequireJsThreadTask
        task.name == 'requireJsThread'
        task.blockExecution == false
    }

    def 'start default thread'() {
        given:
        GroovySpy(RequireJsActor, global: true)
        def actor = Mock(RequireJsActor)

        when:
        task.startThread()

        then:
        1 * RequireJsActor.getInstance() >> actor
        1 * actor.setProperty('convertAction', { it.delegate == task && it.resolveStrategy == Closure.DELEGATE_ONLY})
        2 * actor.start() //????? No idea why. 1 is ok, 2 is strange, maybe super call
        1 * actor.send(project.file(SOURCE).path)
    }

    static final SOURCE = 'source'
    static final DESTINATION = 'destination'
    Project project
    RequireJsThreadTask task

    def setup() {
        project = ProjectBuilder.builder().build()

        task = project.task('requireJsThread', type: RequireJsThreadTask)
        task.sourceFile = SOURCE
        task.destinationFolder = DESTINATION
        project.extensions.requireJs = [classpath: ['src/main/groovy']]
    }
}

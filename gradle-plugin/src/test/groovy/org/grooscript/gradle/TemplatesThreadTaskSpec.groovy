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
import spock.util.concurrent.PollingConditions

import static org.grooscript.util.Util.SEP

class TemplatesThreadTaskSpec extends Specification {

    def 'create the task'() {
        expect:
        task instanceof TemplatesThreadTask
        task.blockExecution == false
    }

    def 'mandatory params'() {
        when:
        task.launchThread()

        then:
        def e = thrown(GradleException)
        e.message.startsWith('For templates, have to define task properties: templatesPath, templates, destinationPath')
    }

    def 'generates templates on start thread'() {
        when:
        def conditions = new PollingConditions()
        task.templatesPath = "src${SEP}test${SEP}resources"
        task.templates = ['one.gtpl']
        task.destinationFile = TEMPLATES_FILE
        task.configureAndStartThread()
        def file = new File(TEMPLATES_FILE)

        then:
        conditions.eventually {
            assert file.text.contains('Templates.templates = gs.map().add("one.gtpl",function(model) {')
            assert file.text.contains('return gs.mc(HtmlBuilder,"build",[function(it) {')
            assert file.text.contains('return gs.mc(Templates,"p",["Hello!"]);')
        }

        cleanup:
        file.delete()
    }

    Project project
    TemplatesThreadTask task
    private static final TEMPLATES_FILE = 'Templates.js'

    def setup() {
        project = ProjectBuilder.builder().build()
        project.extensions.templates = [:]

        task = project.task('templatesThread', type: TemplatesThreadTask)
    }
}

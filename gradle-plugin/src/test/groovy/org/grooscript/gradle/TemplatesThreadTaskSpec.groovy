package org.grooscript.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import static org.grooscript.util.Util.SEP
/**
 * User: jorgefrancoleza
 * Date: 16/11/14
 */
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
        task.project = project
    }
}

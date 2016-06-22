package org.grooscript.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 08/05/15
 */
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

package org.grooscript.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 14/12/13
 */
class ChangesTaskSpec extends Specification {

    Project project
    ChangesTask task

    def setup() {
        project = ProjectBuilder.builder().build()

        task = project.task('spyChanges', type: ChangesTask)
        task.project = project
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

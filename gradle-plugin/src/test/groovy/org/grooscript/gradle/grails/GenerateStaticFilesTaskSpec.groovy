package org.grooscript.gradle.grails

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 25/11/16.
 */
class GenerateStaticFilesTaskSpec extends Specification {

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
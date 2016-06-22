package org.grooscript.gradle.grails

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import spock.lang.Specification

class InitGrailsProcessorSpec extends Specification {

    void 'start grails processor if not a grails project'() {
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
    }
    private File grailsAppFolder = new File('.')
    private Project projectWithoutGrailsFolder = Stub(Project) {
        it.file('grails-app/views') >> null
    }
    private Project projectWithGrails = Stub(Project) {
        it.file('grails-app/views') >> grailsAppFolder
        it.tasks >> taskContainer
        it.task(type: GenerateStaticFiles, "generateGrooscriptGrailsStaticFiles") >> generateStaticFilesTask
    }
    private InitGrailsProcessor initGrailsProcessor = new InitGrailsProcessor()
}
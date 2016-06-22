package org.grooscript.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.grooscript.GrooScript
import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 14/12/13
 */
class ConvertTaskSpec extends Specification {

    static final SOURCE = ['source']
    static final DESTINATION = 'destination'
    Project project
    ConvertTask task

    def setup() {
        project = ProjectBuilder.builder().build()

        task = project.task('convert', type: ConvertTask)
        task.project = project
    }

    def 'create the task'() {
        expect:
        task instanceof ConvertTask
    }

    def 'by default properties come from project.grooscript'() {
        given:
        GroovySpy(GrooScript, global: true)
        project.extensions.grooscript = [source: ['1'], destination: '2', customization: { -> },
                 classpath: ['3'], initialText: 'initial', finalText: 'final',
                 recursive: true, mainContextScope: ['7'], addGsLib: 'grooscript', requireJsModule: false,
                 includeDependencies: true, consoleInfo: true, nashornConsole: true]

        when:
        task.convert()

        then:
        1 * GrooScript.convert([project.file('1')], project.file('2'), [
            customization: project.grooscript.customization,
            classpath: [project.file('3')].collect { it.path },
            initialText: project.grooscript.initialText,
            finalText: project.grooscript.finalText,
            recursive: project.grooscript.recursive,
            mainContextScope: project.grooscript.mainContextScope,
            addGsLib: project.grooscript.addGsLib,
            includeDependencies: project.grooscript.includeDependencies,
            consoleInfo: project.grooscript.consoleInfo,
            nashornConsole: project.grooscript.nashornConsole,
            requireJsModule: false
        ]) >> null
        0 * _
    }

    def 'doesn\'t override task properties'() {
        given:
        GroovySpy(GrooScript, global: true)
        project.extensions.grooscript = [source: ['1'], destination: '2', customization: { -> },
                classpath: ['3'], recursive: false]
        task.source = SOURCE

        when:
        task.convert()

        then:
        1 * GrooScript.convert([project.file(SOURCE[0])], project.file('2'), [
            customization: project.grooscript.customization,
            classpath: [project.file('3')].collect { it.path },
            initialText: null,
            finalText: null,
            recursive: false,
            mainContextScope: null,
            addGsLib: null,
            includeDependencies: false,
            consoleInfo: false,
            requireJsModule: false,
            nashornConsole: false
        ])
    }

    @Unroll
    def 'run the task without source or destination throws error'() {
        when:
        task.source = source
        task.destination = destination
        task.convert()

        then:
        thrown(GradleException)

        where:
        source  |destination
        ['one'] |null
        null    |null
        null    |'two'
    }

    def 'run the task with correct data'() {
        given:
        GroovySpy(GrooScript, global: true)
        project.extensions.grooscript = new ConversionExtension()

        when:
        task.source = SOURCE
        task.destination = DESTINATION
        task.convert()

        then:
        1 * GrooScript.convert([project.file(SOURCE[0])], project.file(DESTINATION), [
                customization: null,
                classpath: [project.file('src/main/groovy').path],
                initialText: null,
                finalText: null,
                recursive: false,
                mainContextScope: null,
                addGsLib: null,
                includeDependencies: false,
                consoleInfo: false,
                requireJsModule: false,
                nashornConsole: false
        ])
    }

    def 'convert tasks with options'() {
        given:
        GroovySpy(GrooScript, global: true)
        task.source = SOURCE
        task.destination = DESTINATION
        task.classpath = ['d']
        task.customization = { true }
        task.initialText = 'initial'
        task.finalText = 'final'
        task.recursive = true
        task.mainContextScope = [',']
        task.addGsLib = 'include'
        task.includeDependencies = true
        task.consoleInfo = true
        task.nashornConsole = true

        when:
        task.convert()

        then:
        1 * GrooScript.convert([project.file(SOURCE[0])], project.file(DESTINATION), [
                customization: task.customization,
                classpath: [project.file('d')].collect { it.path },
                initialText: 'initial',
                finalText: 'final',
                recursive: true,
                mainContextScope: [','],
                addGsLib: 'include',
                includeDependencies: true,
                consoleInfo: true,
                requireJsModule: false,
                nashornConsole: true
        ]) >> null
        0 * _
    }
}

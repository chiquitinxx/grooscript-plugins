package org.grooscript.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.grooscript.convert.ConversionOptions
import org.grooscript.gradle.grails.GenerateStaticFilesTask
import org.grooscript.gradle.grails.InitGrailsProcessor
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 17/12/13
 */
class GrooscriptPluginSpec extends Specification {

    def 'initialization of plugin'() {
        given:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'org.grooscript.conversion'

        expect:
        project.tasks.convert instanceof ConvertTask
        project.tasks.convertThread instanceof ConvertThreadTask
        project.tasks.templatesJs instanceof TemplatesTask
        project.tasks.templatesThread instanceof TemplatesThreadTask
        project.tasks.spyChanges instanceof ChangesTask
        project.tasks.updateGsLibs instanceof UpdateGrooscriptLibsTask
        project.tasks.requireJs instanceof RequireJsTask
        project.tasks.requireJsThread instanceof RequireJsThreadTask
        project.tasks.generateGrailsFiles instanceof GenerateStaticFilesTask
        project.tasks.size() == 9

        and:
        project.extensions.grooscript instanceof ConversionExtension
        project.extensions.templates instanceof TemplatesExtension
        project.extensions.spy instanceof ChangesExtension
        project.extensions.requireJs instanceof RequireJsExtension

        and: 'without changes in conversion options'
        ConversionOptions.values().collect { it.text } ==
                ['classpath', 'customization', 'mainContextScope', 'initialText', 'finalText', 'recursive', 'addGsLib',
                 'requireJsModule', 'consoleInfo', 'includeDependencies', 'nashornConsole']
    }

    def 'grails support'() {
        given:
        GrooscriptPlugin grooscriptPlugin = new GrooscriptPlugin()
        InitGrailsProcessor initGrailsProcessor = Mock(InitGrailsProcessor)
        grooscriptPlugin.initGrailsProcessor = initGrailsProcessor
        Project project = ProjectBuilder.builder().build()

        when:
        grooscriptPlugin.apply(project)

        then:
        1 * initGrailsProcessor.start(project)
        0 * _
    }
}

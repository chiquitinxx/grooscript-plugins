package org.grooscript.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.grooscript.gradle.grails.InitGrailsProcessor
import org.grooscript.gradle.util.InitToolsImpl

/**
 * User: jorgefrancoleza
 * Date: 17/12/13
 */
class GrooscriptPlugin implements Plugin<Project> {

    static final String GROOSCRIPT_GROUP = 'Grooscript'
    InitGrailsProcessor initGrailsProcessor = new InitGrailsProcessor()

    @Override
    void apply(Project project) {
        project.extensions.create('grooscript', ConversionExtension)
        project.extensions.create('templates', TemplatesExtension)
        project.extensions.create('spy', ChangesExtension)
        project.extensions.create('requireJs', RequireJsExtension)
        project.extensions.create('grooscriptGrails', GrailsExtension)
        configureConvertTask(project)
        configureConvertThreadTask(project)
        configureTemplates(project)
        configureTemplatesThread(project)
        configureUpdatesTask(project)
        configureSyncGsLibsTask(project)
        configureRequireJsTask(project)
        configureRequireJsThreadTask(project)
        initGrailsProcessor.start(project)
    }

    private configureConvertTask(Project project) {
        ConvertTask convertTask = project.tasks.create('convert', ConvertTask)
        convertTask.description = 'Convert groovy files to javascript.'
        convertTask.group = GROOSCRIPT_GROUP
    }

    private configureConvertThreadTask(Project project) {
        ConvertThreadTask daemonTask = project.tasks.create('convertThread', ConvertThreadTask)
        daemonTask.description = 'Start a daemon to convert groovy files to javascript if any file changes.'
        daemonTask.group = GROOSCRIPT_GROUP
    }

    private configureTemplates(Project project) {
        TemplatesTask templatesTask = project.tasks.create('templatesJs', TemplatesTask)
        templatesTask.description = 'Generate templates js file.'
        templatesTask.group = GROOSCRIPT_GROUP
    }

    private configureTemplatesThread(Project project) {
        TemplatesThreadTask templatesThreadTask = project.tasks.create('templatesThread', TemplatesThreadTask)
        templatesThreadTask.description = 'Start a daemon to convert groovy templates to javascript if any file changes.'
        templatesThreadTask.group = GROOSCRIPT_GROUP
    }

    private configureUpdatesTask(Project project) {
        ChangesTask updatesTask = project.tasks.create('spyChanges', ChangesTask)
        updatesTask.description = 'Listen changes in files and send notifications.'
        updatesTask.group = GROOSCRIPT_GROUP
    }

    private configureSyncGsLibsTask(Project project) {
        UpdateGrooscriptLibsTask syncGsLibsTask = project.tasks.create('updateGsLibs', UpdateGrooscriptLibsTask)
        syncGsLibsTask.initTools = new InitToolsImpl()
        syncGsLibsTask.description = 'Synchronize grooscript libraries (grooscript.js, grooscript.min.js and ' +
                'grooscript-html-builder.js) with grooscript plugin version.'
        syncGsLibsTask.group = GROOSCRIPT_GROUP
    }

    private configureRequireJsTask(Project project) {
        RequireJsTask requireJsTask = project.tasks.create('requireJs', RequireJsTask)
        requireJsTask.description = 'Convert a file and dependencies to require.js modules'
        requireJsTask.group = GROOSCRIPT_GROUP
    }

    private configureRequireJsThreadTask(Project project) {
        RequireJsThreadTask requireJsThreadTask = project.tasks.create('requireJsThread', RequireJsThreadTask)
        requireJsThreadTask.description = 'Start a daemon to convert require.js modules if any file changes.'
        requireJsThreadTask.group = GROOSCRIPT_GROUP
    }
}

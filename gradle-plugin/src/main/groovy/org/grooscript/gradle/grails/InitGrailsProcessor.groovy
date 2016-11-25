package org.grooscript.gradle.grails

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

class InitGrailsProcessor {

    public static GRAILS_VIEW_FOLDER = 'grails-app/views'

    void start(Project project) {
        if (isGrailsProjectWithGsps(project)) {

            Task assetCompileTask = getGrailsAssetCompileTask(project)
            if (assetCompileTask) {
                Task generateStaticFiles = project.task(
                        type: GenerateStaticFilesTask, 'generateGrooscriptGrailsStaticFiles')
                assetCompileTask.dependsOn(generateStaticFiles)
            }
        }
    }

    private static boolean isGrailsProjectWithGsps(Project project) {
        File grailsViewFolder = project.file(GRAILS_VIEW_FOLDER)
        return grailsViewFolder?.exists() && grailsViewFolder.isDirectory()
    }

    private static Task getGrailsAssetCompileTask(Project project) {
        TaskContainer taskContainer = project.tasks
        taskContainer.getByName('assetCompile')
    }
}
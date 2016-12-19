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
                Task generateStaticFiles = project.tasks.findByName('generateGrailsFiles')
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
        try {
            return taskContainer.getByName('assetCompile')
        } catch (Exception e) {
            project.logger.info("No grails project with asset-pipeline detected to generate static files for it.")
        }
        return null
    }
}
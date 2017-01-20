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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenerateStaticFilesTask extends DefaultTask {

    private static final String NAME = '[Grooscript Gradle Plugin]'
    private GrailsServerPages grailsServerPages = new GrailsServerPages()

    @TaskAction
    void generateGrooscriptStaticFiles() {

        String viewsDir = project.file(InitGrailsProcessor.GRAILS_VIEW_FOLDER)
        logger.debug "$NAME Extracting tags from views dir $viewsDir."
        List<File> gsps = grailsServerPages.find(viewsDir)

        List<Map<String, String>> components = grailsServerPages.getComponents(gsps)

        if (project && components) {
            new ComponentsGenerator(project, logger).generate(components)
        }
    }
}

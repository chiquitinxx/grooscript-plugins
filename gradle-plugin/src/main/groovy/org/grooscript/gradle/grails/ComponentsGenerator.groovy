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
import org.grooscript.grails.core.Conversion
import org.gradle.api.logging.Logger
import org.grooscript.grails.core.GrailsConversions

class ComponentsGenerator {

    private static final String SEP = System.getProperty('file.separator')

    private final Logger logger
    private final Project project
    private final GrailsConversions conversions = new GrailsConversions()

    ComponentsGenerator(final Project project, final Logger logger) {
        this.project = project
        this.logger = logger
    }

    void generate(List<Map<String, String>> components) {
        logger.debug "Generating static files for grails jar / war."
        components.each { Map<String, String> map ->
            String fullClassName = map.src
            String name = map.name
            logger.debug " Generating component $name ..."
            generateComponent(fullClassName, name)
        }
        logger.debug "End generation."
    }

    private void generateComponent(String fullClassName, String name) {
        conversions.setBaseDir(project.projectDir.absolutePath + SEP)
        String jsCode = conversions.convertComponent(fullClassName, name)
        String shortName = conversions.getShortName(fullClassName)
        project.buildDir.mkdirs()
        def (result, errorMessage) = conversions.saveConversionForPackaging(
                project.buildDir,
                shortName + Conversion.COMPONENT_EXTENSION,
                jsCode)
        if (!result) {
            logger.error "Error generating component ($fullClassName) : $errorMessage"
        }
    }
}

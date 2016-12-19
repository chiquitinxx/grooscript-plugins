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
import org.grooscript.grails.core.Conversion
import org.grooscript.grails.core.GrailsConversions

class GenerateStaticFilesTask extends DefaultTask {

    private static final String NAME = '[Grooscript Gradle Plugin]'
    private final GrailsConversions grailsConversions = new GrailsConversions()

    @TaskAction
    void generateGrooscriptStaticFiles() {

        List<String> grooscriptComponents
        List<String> grooscriptRemoteDomains

        if (project && (grooscriptComponents || grooscriptRemoteDomains)) {
            logger.debug "$NAME Generating static files for grails jar / war."
            logger.debug ' Components -> ' + grooscriptComponents.size()
            logger.debug ' RemoteDomains -> ' + grooscriptRemoteDomains.size()
            generateRemoteDomains(grooscriptRemoteDomains)
            logger.debug "$NAME End generation."
        } else {
            logger.debug "$NAME Without components or remote domains for grails jar / war."
        }
    }

    private void generateRemoteDomains(List<String> remoteDomains) {
        remoteDomains.each { domainClassName ->
            String jsCode = grailsConversions.convertRemoteDomainClass(domainClassName)
            String shortName = GrailsConversions.getFileNameFromDomainClassCanonicalName(domainClassName)
            def (result, errorMessage) = grailsConversions.saveConversionForPackaging(
                    project.buildDir,
                    shortName + Conversion.REMOTE_DOMAIN_EXTENSION,
                    jsCode)
            if (!result) {
                logger.error "$NAME Error generating domain class ($domainClassName), error: $errorMessage"
            }
        }
    }
}

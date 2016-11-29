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

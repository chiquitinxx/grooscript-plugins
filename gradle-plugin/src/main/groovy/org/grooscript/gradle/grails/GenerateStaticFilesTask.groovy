package org.grooscript.gradle.grails

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenerateStaticFilesTask extends DefaultTask {

    private static final String NAME = '[Grooscript gradle plugin]'

    @TaskAction
    void generateGrooscriptStaticFiles() {

        List<File> grooscriptComponents
        List<File> grooscriptRemoteDomains

        if (project && (grooscriptComponents || grooscriptRemoteDomains)) {
            logger.debug "$NAME Generating static files for grails jar / war."
            logger.debug ' Components -> ' + grooscriptComponents.size()
            logger.debug ' RemoteDomains -> ' + grooscriptRemoteDomains.size()
            logger.debug "$NAME End generation."
        } else {
            logger.debug "$NAME Without components or remote domains for grails jar / war."
        }
    }
}

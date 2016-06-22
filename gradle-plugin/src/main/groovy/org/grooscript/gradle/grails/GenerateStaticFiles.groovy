package org.grooscript.gradle.grails

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.GrailsExtension

class GenerateStaticFiles extends DefaultTask {

    @TaskAction
    void generateGrooscriptStaticFiles() {
        println '*****************************'
        GrailsExtension extension = project.extensions.grooscriptGrails
        if (project && extensionWithData(extension)) {
            println 'Components -> ' + extension.components
            println 'RemoteDomains -> ' + extension.remoteDomains
            println '*****************************' + project.name
        } else {
            println '[Grooscript gradle plugin] Without components or remote domains for grails jar / war'
        }
    }

    private boolean extensionWithData(GrailsExtension extension) {
        extension && (extension.components || extension.remoteDomains)
    }
}

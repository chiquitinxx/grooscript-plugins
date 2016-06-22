package org.grooscript.gradle

import org.gradle.api.tasks.TaskAction

class TemplatesTask extends TemplatesAbstractTask {

    @TaskAction
    void generateTemplatesJs() {
        checkProperties()
        if (templatesPath && templates && destinationFile) {
            generateTemplate()
        } else {
            errorParameters()
        }
    }
}

package org.grooscript.gradle

import org.gradle.api.tasks.TaskAction

class RequireJsTask extends RequireJsAbstractTask {

    @TaskAction
    void convertRequireJs() {
        checkProperties()
        if (sourceFile && destinationFolder && classpath) {
            convertRequireJsFile()
        } else {
            errorParameters()
        }
    }
}

package org.grooscript.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.daemon.FilesDaemon
import org.grooscript.gradle.changes.ChangesActions

/**
 * User: jorgefrancoleza
 * Date: 17/12/14
 */
class ChangesTask extends DefaultTask {

    List<String> files
    Closure onChanges

    @TaskAction
    void detectChanges() {
        checkProperties()
        if (!files || !onChanges) {
            throw new GradleException("Need define files an action on changes.")
        } else {
            checkingChanges()
        }
    }

    private checkProperties() {
        files = files ?: project.extensions.spy?.files
        if (files)
            files = files.collect { project.file(it).path }
        onChanges = onChanges ?: project.extensions.spy?.onChanges
    }

    private void checkingChanges() {
        new FilesDaemon(files, { listFiles ->
            onChanges.delegate = new ChangesActions()
            onChanges(listFiles)
        }).start()
    }
}

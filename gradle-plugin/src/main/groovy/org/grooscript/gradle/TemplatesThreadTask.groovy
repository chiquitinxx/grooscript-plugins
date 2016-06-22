package org.grooscript.gradle

import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.daemon.FilesDaemon
import org.grooscript.util.GsConsole

/**
 * User: jorgefrancoleza
 * Date: 16/11/14
 */
class TemplatesThreadTask extends TemplatesAbstractTask {

    private static final WAIT_TIME = 100
    boolean blockExecution = false

    @TaskAction
    void launchThread() {
        checkProperties()
        if (templatesPath && templates && destinationFile) {
            configureAndStartThread()
        } else {
            errorParameters()
        }
    }

    protected configureAndStartThread() {
        FilesDaemon filesDaemon
        try {
            filesDaemon = new FilesDaemon(getTemplatesPaths(), getGenerateTemplatesAction(), [actionOnStartup: true])
            filesDaemon.start()
            if (blockExecution) {
                def thread = Thread.start {
                    while (filesDaemon.actor?.isActive()) {
                        sleep(WAIT_TIME)
                    }
                }
                thread.join()
                filesDaemon.stop()
            }
        } catch (e) {
            GsConsole.error("Error in templates thread: ${e.message}")
            filesDaemon?.stop()
        }
    }

    private List<String> getTemplatesPaths() {
        templates.collect {
            project.file("${templatesPath}/${it}").path
        }
    }

    private Closure getGenerateTemplatesAction() {
        { listFilesChanged ->
            if (listFilesChanged) {
                try {
                    generateTemplate()
                } catch(e) {
                    GsConsole.exception "Exception generating templates from thread: ${e.message}"
                }
            }
        }
    }
}

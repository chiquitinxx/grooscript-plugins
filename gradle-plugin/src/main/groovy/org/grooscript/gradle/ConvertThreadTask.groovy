package org.grooscript.gradle

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.daemon.ConversionDaemon
import org.grooscript.gradle.daemon.FilesDaemon
import org.grooscript.util.GsConsole

/**
 * User: jorgefrancoleza
 * Date: 15/12/13
 */
class ConvertThreadTask extends GrooscriptTask {

    private static final WAIT_TIME = 100
    boolean blockExecution = false

    @TaskAction
    void launchDaemon() {
        checkProperties()
        if (!source || !destination) {
            throw new GradleException("Need define source and destination.")
        } else {
            configureAndStartDaemon()
        }
    }

    private configureAndStartDaemon() {
        FilesDaemon filesDaemon
        try {
            filesDaemon = ConversionDaemon.start(source.collect { project.file(it).path },
                    project.file(destination).path, conversionProperties)
            Thread.sleep(WAIT_TIME)
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
            GsConsole.error("Error in conversion daemon: ${e.message}")
            filesDaemon?.stop()
        }
    }
}

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
package org.grooscript.gradle

import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.daemon.FilesDaemon
import org.grooscript.util.GsConsole

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

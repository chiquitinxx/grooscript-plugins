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
package org.grooscript.gradle.daemon

import org.grooscript.util.GsConsole

import java.util.concurrent.TimeUnit

import static groovyx.gpars.dataflow.Dataflow.task

class FilesDaemon {

    private static final WAIT_TIME = 200
    final List<String> files
    final Closure action
    Map options = [
        time: WAIT_TIME,
        actionOnStartup: false,
        recursive: false,
        checkDependencies: false
    ]
    FilesActor actor

    FilesDaemon(List<String> files, Closure action, Map options = [:]) {
        this.files = files
        this.action = action
        options.each { key, value ->
            this.options[key] = value
        }
    }

    void start() {
        if (options.actionOnStartup == true) {
            try {
                action files
            } catch (e) {
                GsConsole.error("Error executing action at start in files (${files}): ${e.message}")
            }
        }
        task {
            actor = new FilesActor(action: action, restTime: options.time).start()
            actor << files
            GsConsole.message('Listening file changes in : ' + files)
        }
    }

    void stop() {
        if (actor && actor.isActive()) {
            actor << FilesActor.FINISH
        }
    }
}

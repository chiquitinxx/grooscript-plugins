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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.daemon.FilesDaemon
import org.grooscript.gradle.changes.ChangesActions

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

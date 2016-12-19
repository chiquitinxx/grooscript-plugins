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
import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.util.InitTools

import static groovy.io.FileType.*

class UpdateGrooscriptLibsTask extends DefaultTask {

    InitTools initTools
    final List<String> filesToSync = ['grooscript.js', 'grooscript.min.js', 'grooscript-html-builder.js']
    final excludedDirs = ['.svn', '.git', '.hg', '.idea', project.buildDir.name, 'out']

    @TaskAction
    void sync() {
        checkProjectFolderFiles()
        project.projectDir.eachDir {
            if (!(it.name in excludedDirs)) {
                it.traverse([
                        type: FILES
                ], { file ->
                    if (file.name in filesToSync) {
                        initTools.extractGrooscriptJarFile(file.name, file.path)
                    }
                })
            }
        }
    }

    private checkProjectFolderFiles() {
        project.projectDir.eachFile { file ->
            if (file.name in filesToSync) {
                initTools.extractGrooscriptJarFile(file.name, file.path)
            }
        }
    }
}

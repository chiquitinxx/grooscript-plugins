package org.grooscript.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.util.InitTools

import static groovy.io.FileType.*
/**
 * User: jorgefrancoleza
 * Date: 23/1/15
 */
class SyncGrooscriptLibsTask extends DefaultTask {

    InitTools initTools
    final List<String> filesToSync = ['grooscript.js', 'grooscript.min.js', 'grooscript-tools.js']
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

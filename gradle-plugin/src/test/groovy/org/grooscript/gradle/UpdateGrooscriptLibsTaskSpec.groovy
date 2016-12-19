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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.grooscript.gradle.util.InitTools
import spock.lang.Specification
import spock.lang.Unroll

import static org.grooscript.util.Util.SEP

class UpdateGrooscriptLibsTaskSpec extends Specification {

    Project project
    UpdateGrooscriptLibsTask task
    InitTools initTools

    private final static EMPTY_CONTENT = ''
    private final static TEMP_FOLDER = 'one'

    def setup() {
        initTools = Mock(InitTools)
        project = ProjectBuilder.builder().withProjectDir(new File('.')).build()
        task = project.task('updateGsLibs', type: UpdateGrooscriptLibsTask)
        task.initTools = initTools
        task.project = project
    }

    void 'create the task'() {
        expect:
        task instanceof UpdateGrooscriptLibsTask
        task.filesToSync == ['grooscript.js', 'grooscript.min.js', 'grooscript-html-builder.js']
    }

    @Unroll
    void 'sync files'() {
        given:
        createFileEmpty(nameFile)

        when:
        task.sync()

        then:
        1 * initTools.extractGrooscriptJarFile(new File(nameFile).name, new File(nameFile).absolutePath )

        cleanup:
        new File(nameFile).delete()
        new File(TEMP_FOLDER).deleteDir()

        where:
        nameFile << ['grooscript.js', 'grooscript.min.js', 'grooscript-html-builder.js',
                     "${TEMP_FOLDER}${SEP}grooscript.js", "${TEMP_FOLDER}${SEP}grooscript.min.js",
                     "${TEMP_FOLDER}${SEP}two${SEP}grooscript-html-builder.js"]
    }

    @Unroll
    void 'files that not to be updated'() {
        given:
        createFileEmpty(nameFile)

        when:
        task.sync()

        then:
        0 * initTools.extractGrooscriptJarFile(new File(nameFile).name, new File(nameFile).absolutePath )

        cleanup:
        new File(nameFile).delete()
        new File(TEMP_FOLDER).deleteDir()

        where:
        nameFile << ['pepe.js', 'grooscript.gol', "${TEMP_FOLDER}${SEP}jquery.min.js", 'grooscript.min']
    }

    private createFileEmpty(name) {
        new File("${TEMP_FOLDER}${SEP}two").mkdirs()
        new File(name).text = EMPTY_CONTENT
    }
}

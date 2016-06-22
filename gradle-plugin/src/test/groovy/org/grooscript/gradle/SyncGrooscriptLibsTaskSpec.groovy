package org.grooscript.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.grooscript.gradle.util.InitTools
import spock.lang.Specification
import spock.lang.Unroll

import static org.grooscript.util.Util.SEP

/**
 * Created by jorgefrancoleza on 23/1/15.
 */
class SyncGrooscriptLibsTaskSpec extends Specification {

    Project project
    SyncGrooscriptLibsTask task
    InitTools initTools

    private final static EMPTY_CONTENT = ''
    private final static TEMP_FOLDER = 'one'

    def setup() {
        initTools = Mock(InitTools)
        project = ProjectBuilder.builder().withProjectDir(new File('.')).build()
        task = project.task('syncGsLibs', type: SyncGrooscriptLibsTask)
        task.initTools = initTools
        task.project = project
    }

    void 'create the task'() {
        expect:
        task instanceof SyncGrooscriptLibsTask
        task.filesToSync == ['grooscript.js', 'grooscript.min.js', 'grooscript-tools.js']
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
        nameFile << ['grooscript.js', 'grooscript.min.js', 'grooscript-tools.js',
                     "${TEMP_FOLDER}${SEP}grooscript.js", "${TEMP_FOLDER}${SEP}grooscript.min.js",
                     "${TEMP_FOLDER}${SEP}two${SEP}grooscript-tools.js"]
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

package org.grooscript.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class GrailsFunctionalSpec extends Specification {

    protected static final String SEP = System.getProperty('file.separator')
    private static final String NAME_GRAILS_PROJECT = 'test'
    private static final String GRAILS_VERSION = '3.2.3'

    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()

    def setup() {
        downloadAndUnzipGrailsProject(testProjectDir.newFile("${NAME_GRAILS_PROJECT}.zip"))
    }

    String getGrailsProjectDir() {
        testProjectDir.root.absolutePath + SEP + NAME_GRAILS_PROJECT
    }

    private void downloadAndUnzipGrailsProject(File zipFile) {
        zipFile.withOutputStream { out ->
            out << new URL ("http://start.grails.org/generate?name=${NAME_GRAILS_PROJECT}&" +
                    "version=${GRAILS_VERSION}&profile=web&features=asset-pipeline").openStream()
        }

        AntBuilder ant = new AntBuilder()
        ant.unzip(src: zipFile.absolutePath,
                dest: testProjectDir.root.absolutePath,
                overwrite:"true" )
    }

    BuildResult runWithArguments(String arguments) {
        GradleRunner.create()
                .withGradleVersion('2.14')
                .withProjectDir(new File(grailsProjectDir))
                .withArguments(arguments)
                .withDebug(true)
                .withPluginClasspath()
                .build()
    }
}

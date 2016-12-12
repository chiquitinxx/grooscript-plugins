package org.grooscript.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Created by jorge on 22/8/15.
 */
abstract class AbstractFunctionalSpec extends Specification {

    protected static final String SEP = System.getProperty('file.separator')

    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile.text = """
plugins {
  id "org.grooscript.conversion"
}
"""
    }

    void copyTestResourcesFiles(...files) {
        ClassLoader classLoader = getClass().getClassLoader()
        files.each { String nameFile ->
            def file = testProjectDir.newFile(nameFile)
            file.text = new File(classLoader.getResource(nameFile).getFile()).text
        }
    }

    BuildResult runWithArguments(String arguments) {
        GradleRunner.create()
                .withGradleVersion('2.13')
                .withProjectDir(testProjectDir.root)
                .withArguments(arguments)
                .withPluginClasspath()
                .build()
    }
}

package org.grooscript.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.grooscript.util.Util.SEP as LS

/**
 * Created by jorge on 22/8/15.
 */
abstract class AbstractFunctionalSpec extends Specification {

    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    String testResourcesFilesFolder = "src${LS}functTest${LS}resources${LS}"
    List<File> pluginClasspath

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile.text = """
plugins {
  id "org.grooscript.conversion"
}
"""

        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        pluginClasspath = pluginClasspathResource.readLines()
                .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { new File(it) }
    }

    void copyTestResourcesFiles(...files) {
        files.each { nameFile ->
            def file = testProjectDir.newFile(nameFile)
            file.text = new File(testResourcesFilesFolder + nameFile).text
        }
    }

    BuildResult runWithArguments(String arguments) {
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(arguments)
                .withPluginClasspath(pluginClasspath)
                .build()
    }
}

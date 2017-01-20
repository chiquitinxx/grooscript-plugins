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

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

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

    void copyTestResourcesFile(String resourceFileName, String pathDestination) {
        testProjectDir.newFolder(*(pathDestination.split('/')))
        ClassLoader classLoader = getClass().getClassLoader()
        def file = testProjectDir.newFile(pathDestination + SEP + resourceFileName)
        file.text = new File(classLoader.getResource(resourceFileName).getFile()).text
    }

    BuildResult runWithArguments(String arguments) {
        GradleRunner.create()
                .withGradleVersion('2.13')
                .withProjectDir(testProjectDir.root)
                .withArguments(arguments)
                .withPluginClasspath()
                .withDebug(true)
                .build()
    }
}

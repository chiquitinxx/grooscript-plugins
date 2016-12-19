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

abstract class GrailsFunctionalSpec extends Specification {

    protected static final String SEP = System.getProperty('file.separator')
    private static final String NAME_GRAILS_PROJECT = 'test'
    private static final String GRAILS_VERSION = '3.2.3'

    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()

    def setup() {
        System.setProperty('org.gradle.testkit.debug', 'true')
        downloadAndUnzipGrailsProject(testProjectDir.newFile("${NAME_GRAILS_PROJECT}.zip"))
        addPluginInfoInBuildFile()
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
                overwrite: "true")
    }

    private void addPluginInfoInBuildFile() {
        File buildFile = new File(grailsProjectDir + SEP + 'build.gradle')
        buildFile.text = buildFile.text.replaceAll(/version \"0\.1\"/,'''plugins {
            id 'org.grooscript.conversion'
        }
        version "0.1"''')

    }

    BuildResult runWithArguments(String arguments) {
        GradleRunner.create()
                .withGradleVersion('2.14')
                .withProjectDir(new File(grailsProjectDir))
                .withArguments(arguments)
                .withPluginClasspath()
                .build()
    }
}

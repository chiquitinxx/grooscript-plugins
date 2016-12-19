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
package org.grooscript.gradle.daemon

import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions

import static org.grooscript.util.Util.SEP

class ConversionDaemonSpec extends Specification {

    def 'execution if no files changed'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FILE, CONVERSION_OPTIONS_EMPTY, [])

        then:
        0 * _
    }

    def 'execution if one file changes and destination is a file'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FILE, CONVERSION_OPTIONS_EMPTY, [FILE1])

        then:
        1 * GrooScript.convert(_, _, CONVERSION_OPTIONS_EMPTY)
    }

    def 'execution with conversion options and destination is a file'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FILE, conversionOptions, [FILE1])

        then:
        1 * GrooScript.convert(SOURCE, DESTINATION_FILE, conversionOptions)
    }

    def 'execution with conversion option includeDependencies equals to true'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FOLDER, [includeDependencies: true], [FILE1])

        then:
        1 * GrooScript.convert(SOURCE, DESTINATION_FOLDER, [includeDependencies: true])
    }

    def 'execution if one file changes and destination is a folder'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FOLDER, CONVERSION_OPTIONS_EMPTY, [FILE1])

        then:
        1 * GrooScript.convert([FILE1], DESTINATION_FOLDER, CONVERSION_OPTIONS_EMPTY)
    }

    @Unroll
    def 'not call conversion if change a file that is not a groovy or java file'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FILE, conversionOptions, [file])

        then:
        0 * _

        where:
        file << ['file', 'file.js', 'file.html']
    }

    def 'converts files on start'() {
        given:
        createFiles()

        when:
        def daemon = ConversionDaemon.start(SOURCE, DESTINATION_FOLDER, CONVERSION_OPTIONS_EMPTY)

        then:
        filesConverted()
        ConversionDaemon.numberConversions == old(ConversionDaemon.numberConversions) + 1

        cleanup:
        daemon.stop()
        deleteFilesAndDestination()
    }

    def 'converts with include dependencies conversion option'() {
        given:
        def newConversionOptions = conversionOptions.clone()
        newConversionOptions[ConversionOptions.INCLUDE_DEPENDENCIES.text] = true
        createFiles()

        when:
        def daemon = ConversionDaemon.start([FILE3], DESTINATION_FOLDER, newConversionOptions)
        def resultFile = new File("${DESTINATION_FOLDER}${SEP}Class3.js")

        then:
        resultFile.text.contains('function Class2() {')

        when:
        sleep(1000)
        def conditions = new PollingConditions()
        new File(FILE2).text = 'class Class2 { def helloWorld }'

        then:
        conditions.eventually {
            def file = new File("${DESTINATION_FOLDER}${SEP}Class3.js")
            assert file.text.contains('helloWorld')
        }

        cleanup:
        daemon.stop()
        deleteFilesAndDestination()
    }

    def 'converts a folder with include dependencies conversion option'() {
        given:
        def newConversionOptions = conversionOptions.clone()
        newConversionOptions[ConversionOptions.INCLUDE_DEPENDENCIES.text] = true
        createFiles()

        when:
        def daemon = ConversionDaemon.start(['.'], DESTINATION_FOLDER, newConversionOptions)
        def resultFile = new File("${DESTINATION_FOLDER}${SEP}Class3.js")

        then:
        resultFile.text.contains('function Class2() {')
        resultFile.text.contains('function Class3() {')

        cleanup:
        daemon.stop()
        deleteFilesAndDestination()
    }

    private static final FILE1 = 'Class1.groovy'
    private static final FILE2 = 'Class2.groovy'
    private static final FILE3 = 'Class3.groovy'
    private static final SOURCE = [FILE1, FILE2]
    private static final DESTINATION_FILE = 'file.js'
    private static final DESTINATION_FOLDER = 'folder'
    private static final CONVERSION_OPTIONS_EMPTY = [:]
    private getConversionOptions() {
        def map = [:]
        map[ConversionOptions.CLASSPATH.text] = '.'
        map
    }

    private createFiles() {
        new File(FILE1) << 'class Class1 {}'
        new File(FILE2) << 'class Class2 {}'
        new File(FILE3) << 'class Class3 { Class2 class2 = new Class2() }'
    }

    private boolean filesConverted() {
        new File("${DESTINATION_FOLDER}${SEP}Class1.js").exists() &&
        new File("${DESTINATION_FOLDER}${SEP}Class2.js").exists()
    }

    private deleteFilesAndDestination() {
        new File(DESTINATION_FOLDER).deleteDir()
        new File(FILE1).delete()
        new File(FILE2).delete()
        new File(FILE3).delete()
        new File(DESTINATION_FILE).delete()
    }
}

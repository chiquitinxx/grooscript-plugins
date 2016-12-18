package org.grooscript.gradle

import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Unroll
class ConversionsFunctionalSpec extends AbstractFunctionalSpec {

    void "convert files"() {
        given:
        copyTestResourcesFiles('C.groovy', 'UseC.groovy', 'E.groovy')
        buildFile << """
grooscript {
    source = ['${fileToConvert}.groovy']
    destination = '.'
    classpath = ['.']
}"""

        when:
        def result = runWithArguments('convert')
        def generatedFile = new File(testProjectDir.root.absolutePath + SEP + fileToConvert + '.js')

        then:
        result.task(":convert").outcome == SUCCESS

        and:
        generatedFile.exists()

        where:
        fileToConvert << ['C', 'UseC', 'E']
    }
}

package org.grooscript.gradle

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

/**
 * Created by jorge on 22/8/15.
 */
class ConversionsFunctionalSpec extends AbstractFunctionalSpec {

    private static final String SEP = System.getProperty('file.separator')

    def "convert files"() {
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

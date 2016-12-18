package org.grooscript.gradle

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class ComponentsFunctionalSpec extends GrailsFunctionalSpec {

    void 'download grails project and build it'() {
        given:
        File grailsDir = new File(grailsProjectDir)

        expect:
        grailsDir.isDirectory()
        new File(grailsProjectDir + SEP + 'build.gradle').exists()

        when:
        def result = runWithArguments('build')

        then:
        result.task(":build").outcome == SUCCESS
    }
}

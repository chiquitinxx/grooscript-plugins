package org.grooscript.gradle

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class GrailsComponentsFunctionalSpec extends GrailsFunctionalSpec {

    void 'download grails project and build it'() {
        given:
        File grailsDir = new File(grailsProjectDir)
        File buildFile = new File(grailsProjectDir + SEP + 'build.gradle')

        expect:
        grailsDir.isDirectory()
        buildFile.exists()
        buildFile.text.concat('id \'org.grooscript.conversion\'')

        when:
        def result = runWithArguments('build')

        then:
        result.task(":build").outcome == SUCCESS
    }
}

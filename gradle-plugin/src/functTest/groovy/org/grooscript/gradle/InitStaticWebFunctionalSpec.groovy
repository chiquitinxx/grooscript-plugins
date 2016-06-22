package org.grooscript.gradle

import static org.gradle.testkit.runner.TaskOutcome.*
import static org.grooscript.gradle.InitStaticWebTask.*

/**
 * Created by jorge on 22/8/15.
 */
class InitStaticWebFunctionalSpec extends AbstractFunctionalSpec {

    def "init static web task and convert Presenter.groovy file"() {
        when:
        def result = runWithArguments('initStaticWeb')

        then:
        result.output.contains('Generation completed.')
        result.task(":initStaticWeb").outcome == SUCCESS

        when:
        result = runWithArguments('convert')
        def generatedFile = new File(testProjectDir.root.absolutePath + SEP + JS_APP_DIR + SEP + 'Presenter.js')

        then:
        generatedFile.exists()
        generatedFile.text.startsWith('function Presenter() {')
        result.task(":convert").outcome == SUCCESS
    }
}

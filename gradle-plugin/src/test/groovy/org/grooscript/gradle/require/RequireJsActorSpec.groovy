package org.grooscript.gradle.require

import org.grooscript.convert.util.ConvertedFile
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

/**
 * Created by jorgefrancoleza on 23/5/15.
 */
class RequireJsActorSpec extends Specification {

    void 'starts daemon'() {
        given:
        def executions = 0
        def conditions = new PollingConditions(timeout: 2)
        actor.convertAction = {
            executions++
            convertedFiles
        }

        when:
        actor.start()
        actor << fileName

        then:
        conditions.eventually {
            assert actor.isActive()
            assert executions == 1
            assert actor.listFiles == convertedFiles
        }

        cleanup:
        actor.stop()
    }

    void 'if raises exception, actor continues'() {
        given:
        def executions = 0
        def conditions = new PollingConditions(timeout: 2)
        actor.convertAction = {
            executions++
            throw new Exception()
        }

        when:
        actor.start()
        actor << fileName

        then:
        conditions.eventually {
            assert actor.isActive()
            assert executions > 2
        }

        cleanup:
        actor.stop()
    }

    private RequireJsActor actor = new RequireJsActor()
    private fileName = 'build.gradle'
    private convertedFiles = [new ConvertedFile(fileName, fileName)]
}

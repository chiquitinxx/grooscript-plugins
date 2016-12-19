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
package org.grooscript.gradle.require

import org.grooscript.convert.util.ConvertedFile
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

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

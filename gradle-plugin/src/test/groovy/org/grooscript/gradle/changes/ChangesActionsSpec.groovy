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
package org.grooscript.gradle.changes

import org.grooscript.gradle.websocket.Client
import spock.lang.Specification

class ChangesActionsSpec extends Specification {

    void 'test send via websocket'() {
        given:
        GroovySpy(Client, global: true)

        when:
        actions.springWebsocketTo(url).data(data).onChannel(channel)

        then:
        1 * Client.connectAndSend(url, channel, data) >> null
    }

    String url = 'ws://localhost'
    def data = 'any data'
    def channel = '/channel'
    ChangesActions actions = new ChangesActions()
}

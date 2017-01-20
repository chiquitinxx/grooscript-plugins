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
package org.grooscript.grails.event

import grails.events.Events
import org.grooscript.grails.util.GrooscriptGrailsHelpers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import reactor.bus.Event

import static reactor.bus.selector.Selectors.*

class GrooscriptEventsInterceptor implements Events {

    private Set<String> eventsListening

    private GrooscriptGrailsHelpers grooscriptGrailsHelpers
    private ApplicationContext applicationContext

    @Autowired
    GrooscriptEventsInterceptor(
            GrooscriptGrailsHelpers grooscriptGrailsHelpers,
            ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
        this.grooscriptGrailsHelpers = grooscriptGrailsHelpers
    }

    synchronized void addEventToIntercept(String name) {
        if (!eventsListening) {
            eventsListening = [] as Set
            if (grooscriptGrailsHelpers.isSpringWebsocketsActive(applicationContext)) {
                on(regex('.*')) { Event event ->
                    checkIfListeningEvent(event)
                }
            }
        }
        eventsListening << name
    }

    synchronized private void checkIfListeningEvent(Event event) {
        if (eventsListening.contains(event.key)) {
            sendEventToWebsocketClients(event)
        }
    }

    private void sendEventToWebsocketClients(Event event) {
        grooscriptGrailsHelpers.sendWebsocketEventMessage(
                websocketGrooscriptPath + event.key,
                event.data
        )
    }

    private String getWebsocketGrooscriptPath() {
        grooscriptGrailsHelpers.grooscriptWebsocketTopicPrefix
    }
}

package org.grooscript.grails.event

import grails.events.Events
import org.grooscript.grails.util.GrooscriptGrailsHelpers
import org.springframework.beans.factory.annotation.Autowired
import reactor.bus.Event

import static reactor.bus.selector.Selectors.*

class GrooscriptEventsInterceptor implements Events {

    private Set<String> eventsListening

    private GrooscriptGrailsHelpers grooscriptGrailsHelpers

    @Autowired
    GrooscriptEventsInterceptor(GrooscriptGrailsHelpers grooscriptGrailsHelpers) {

        this.grooscriptGrailsHelpers = grooscriptGrailsHelpers
    }

    synchronized public void addEventToIntercept(String name) {
        if (!eventsListening) {
            eventsListening = [] as Set
            if (grooscriptGrailsHelpers.springWebsocketsActive) {
                on(regex('.*')) { Event event ->
                    checkIfListeningEvent(event)
                }
            }
        }
        eventsListening << name
    }

    synchronized private void checkIfListeningEvent(Event event) {
        println '* EventsInterceptor ' + event.key + ' - ' + event.data
        if (eventsListening.contains(event.key)) {
            grooscriptGrailsHelpers.sendWebsocketMessake((String)event.key, event.data)
        }
    }
}

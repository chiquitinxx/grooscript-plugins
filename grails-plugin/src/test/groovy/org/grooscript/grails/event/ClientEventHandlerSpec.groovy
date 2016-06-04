package org.grooscript.grails.event

import spock.lang.Specification

class ClientEventHandlerSpec extends Specification {

    static final NAME_EVENT = 'nameEvent'
    static final FAKE_EVENT = 'FAKE'

    def 'test client events'() {
        given:
        def events = new ClientEventHandler()
        def value = 0
        Closure action = { item -> value++; println item }

        when:
        events.onEvent(NAME_EVENT, action)

        and:
        events.sendMessage(NAME_EVENT, [data: 0])
        events.sendMessage(FAKE_EVENT, [data: 1])

        then:
        value == 1
    }
}

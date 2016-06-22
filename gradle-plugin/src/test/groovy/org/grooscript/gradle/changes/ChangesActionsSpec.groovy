package org.grooscript.gradle.changes

import org.grooscript.gradle.websocket.Client
import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 18/12/14.
 */
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

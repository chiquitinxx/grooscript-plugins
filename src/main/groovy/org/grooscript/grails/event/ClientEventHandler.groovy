package org.grooscript.grails.event

import groovy.transform.TypeChecked

import javax.annotation.ParametersAreNonnullByDefault

/**
 * User: jorgefrancoleza
 * Date: 14/09/13
 */
@TypeChecked
@ParametersAreNonnullByDefault
class ClientEventHandler implements EventHandler {

    private Map<String, List<Closure>> mapHandlers = [:]

    void sendMessage(String channel, Map data) {
        if (mapHandlers[channel]) {
            mapHandlers[channel].each { Closure action ->
                action(data)
            }
        }
    }

    void onEvent(String channel, Closure action) {
        if (!mapHandlers[channel]) {
            mapHandlers[channel] = []
        }
        mapHandlers[channel] << action
    }

    void close() {
        mapHandlers = [:]
    }
}

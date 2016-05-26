package org.grooscript.grails.event

/**
 * @author Jorge Franco <jorge.franco@osoco.es>
 */
interface EventHandler {
    void sendMessage(String channel, Map data)
    void onEvent(String channel, Closure action)
    void close()
}

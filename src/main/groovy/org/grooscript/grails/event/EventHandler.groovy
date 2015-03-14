package org.grooscript.grails.event

/**
 * @author Jorge Franco
 * Date: 09/08/13
 */
interface EventHandler {
    void sendMessage(String channel, Map data)
    void onEvent(String channel, Closure action)
    void close()
}

package org.grooscript.grails.event

interface EventHandler {
    void sendMessage(String channel, Map data)
    void onEvent(String channel, Closure action)
    void close()
}

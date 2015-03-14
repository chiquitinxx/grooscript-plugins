package org.grooscript.grails.websocket

/**
 * Created by jorge on 20/07/14.
 */
interface WebsocketSender {
    void send(String path, Object data)
}

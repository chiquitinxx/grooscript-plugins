package org.grooscript.grails

import org.grooscript.grails.event.WsMessage

class GsWebsocketController {

    @org.springframework.messaging.handler.annotation.MessageMapping("/gswsevent")
    protected void handleGrooscriptWebsocketEvent(WsMessage message) {
        //Event from websocket clients, send to grails events
        notify message.name, message.data
    }
}

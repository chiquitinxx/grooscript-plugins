package org.grooscript.grails.util

import org.grooscript.asts.GsNative

class GrooscriptGrailsEvents {

    @GsNative
    static void sendClientMessage(String channel, message) {/*
        var sendMessage = message;
        if (!gs.isGroovyObj(message)) {
            sendMessage = gs.toGroovy(message);
        }
        gsEvents.sendMessage(channel, sendMessage);
    */}

    @GsNative
    static void sendWebsocketMessage(String channel, message) {/*
        var sendMessage = message;
        if (gs.isGroovyObj(message)) {
            sendMessage = gs.toJavascript(message);
        }
        websocketClient.send(channel, {}, JSON.stringify(sendMessage));
    */}
}

package org.grooscript.gradle.changes

import org.grooscript.gradle.websocket.Client

/**
 * Created by jorgefrancoleza on 18/12/14.
 */
class ChangesActions {
    WebsocketAddress springWebsocketTo(String url) {
        new WebsocketAddress(url)
    }
}


class WebsocketAddress {
    def url

    WebsocketAddress(url) {
        this.url = url
    }

    WebsocketAddressData data(data) {
        new WebsocketAddressData(url, data)
    }
}

class WebsocketAddressData {
    def url, data

    WebsocketAddressData(url, data) {
        this.url = url
        this.data = data
    }

    def onChannel(String channel) {
        Client.connectAndSend(url, channel, data)
    }
}
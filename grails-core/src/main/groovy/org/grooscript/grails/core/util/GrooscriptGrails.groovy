package org.grooscript.grails.core.util

import org.grooscript.asts.GsNative

class GrooscriptGrails {

    static long count = 0
    static Map components = [:]
    static def websocketClient = null
    static String wsPrefix = "/app"

    static register(component) {
        def number = count++
        component.cId = number
        components["id${number}"] = component
        component
    }

    static recover(cId) {
        components["id${cId}"]
    }

    @GsNative
    static void doRemoteCall(String controller, String action, params, onSuccess, onFailure) {/*
        var url = '/' + controller;
        if (action !== null) {
            url = url + '/' + action;
        }
        GsHlp.http(url, "POST", params, onSuccess, onFailure);
    */}

    @GsNative
    static void createComponent(componentClass, name) {/*
        var component = Object.create(HTMLElement.prototype);
        component.createdCallback = function() {
            var shadow = this.createShadowRoot();
            var content = this.textContent;
            var attrs = this.attributes; //name and value
            var map = {shadowRoot: shadow, content: content};
            if (attrs && attrs.length > 0) {
                for (var i = 0; i < attrs.length; i++) {
                    var element = attrs[i];
                    map[element.name] = element.value;
                }
            }
            GrooscriptGrails.register(componentClass(map)).render();
        };
        document.registerElement(name, {prototype: component});
    */}

    @GsNative
    static void sendWebsocketMessage(String path, message) {/*
        var sendMessage = message;
        if (GrooscriptGrails.websocketClient === null) {
            console.log("Websocket connection not up!");
        } else {
            if (gs.isGroovyObj(message)) {
                sendMessage = gs.toJavascript(message);
            }
            GrooscriptGrails.websocketClient.send(path, {}, JSON.stringify(sendMessage));
        }
    */}

    static void notifyEvent(String eventName, data) {
        sendWebsocketMessage(wsPrefix + "/gswsevent", ['name': eventName, 'data': data])
    }

    static findComponentById(String id) {
        components.find { key, value ->
            value.id == id
        }?.value
    }
}

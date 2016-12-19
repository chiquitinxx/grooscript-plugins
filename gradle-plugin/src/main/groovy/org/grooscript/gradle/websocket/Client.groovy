/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.gradle.websocket

import org.grooscript.util.GsConsole
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.*
import org.springframework.web.socket.sockjs.client.*
import org.springframework.web.socket.client.standard.StandardWebSocketClient

import java.nio.charset.Charset

class Client {

    /**
     * Connect to a webSocket url and send a message on STOMP
     * @param webSocketUrl ws://... or http://....
     * @param destination Stomp destination
     * @param message Any object
     * @return
     */
    static connectAndSend(String webSocketUrl, String destination, message) {
        try {
            //Try connect to URL
            webSocketUrl.toURL().text
            List<Transport> transports = new ArrayList<>(2)
            transports.add(new WebSocketTransport(new StandardWebSocketClient()))
            transports.add(new RestTemplateXhrTransport())

            SockJsClient sockJsClient = new SockJsClient(transports)
            sockJsClient.doHandshake(new MyWebSocketHandler(destination, message), webSocketUrl)
        } catch (e) {
            GsConsole.error "Error connecting to websocket: $webSocketUrl"
        }
    }
}

class MyWebSocketHandler implements WebSocketHandler {

    private final MessageConverter messageConverter = new MappingJackson2MessageConverter()
    private final StompEncoder encoder = new StompEncoder()
    private static final Charset UTF_8 = Charset.forName("UTF-8")

    def message
    String destination

    MyWebSocketHandler(String destination, message) {
        this.message = message
        this.destination = destination
    }

    @Override
    void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //println '******************* CONNECTED *************** '+session.class.name

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND)
        headers.setDestination(destination)
        sendInternal(session,
                (Message<byte[]>)messageConverter.toMessage(message, new MessageHeaders(headers.toMap())))
        session.close()
    }

    private void sendInternal(WebSocketSession session, Message<byte[]> message) {
        byte[] bytes = this.encoder.encode(message)
        try {
            session.sendMessage(new TextMessage(new String(bytes, UTF_8)))
        }
        catch (IOException e) {
            throw new IllegalStateException(e)
        }
    }

    @Override
    void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        //println ' *** MSG ***'+message
    }

    @Override
    void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        GsConsole.error("Error sending message: '$message' on Spring WebSocket - ${exception.message}")
    }

    @Override
    void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        //println ' ** CLOSED ** '
    }

    @Override
    boolean supportsPartialMessages() {
        return false
    }
}

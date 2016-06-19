package org.grooscript.grails.event

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.ChannelInterceptorAdapter

class WebsocketInterceptor extends ChannelInterceptorAdapter {
    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
        println '** Intercepted message ' + message.payload
        println '**** headers ' + message.headers
    }
}

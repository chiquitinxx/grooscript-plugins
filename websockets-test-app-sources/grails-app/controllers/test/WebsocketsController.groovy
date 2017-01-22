package test

import grails.events.Events
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

class WebsocketsController implements Events {

    def index() { }

    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    protected String hello(String world) {
        return "hello from controller, ${world}!"
    }
}

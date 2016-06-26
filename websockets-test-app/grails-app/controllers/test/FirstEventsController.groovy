package test

import grails.events.Events
import org.springframework.messaging.simp.SimpMessagingTemplate

import java.time.LocalDateTime
import java.time.ZoneOffset

class FirstEventsController implements Events {

    SimpMessagingTemplate brokerMessagingTemplate

    def date = LocalDateTime.of(2012, 9, 21, 12, 23, 31)

    def index() { }

    def doEvents() {
        notify "hello", "hello from service!"
        brokerMessagingTemplate.convertAndSend "/topic/gswsevent/gotMap",
                [a: "a", b: 1.23, c: Date.from(date.toInstant(ZoneOffset.UTC)) ]
        render "Ok"
    }
}

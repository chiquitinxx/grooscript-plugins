package test

import grails.events.Events

import java.time.LocalDateTime
import java.time.ZoneOffset

class FirstEventsController implements Events {

    def date = LocalDateTime.of(2012, 9, 21, 12, 23, 31)

    def index() { }

    def doEvents() {
        notify "hello", "hello from service!"
        notify "gotmap", [a: "a", b: 1.23, c: Date.from(date.toInstant(ZoneOffset.UTC)) ]
        render "Ok"
    }
}

package test

import grails.events.Events

class FirstEventsController implements Events {

    def index() { }

    def doEvents() {
        notify "hello", "hello from service!"
        notify "gotmap", [a: "a", b: 1.23 ]
        render "Ok"
    }
}

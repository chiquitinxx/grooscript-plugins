package test

import grails.converters.JSON

class ResponsesController {

    def index() { }

    def simpleJson() {
        render ([action: 'Hello', who: 'Wold!', times: 1] as JSON)
    }

    def text() {
        render 'Hello in text!'
    }

    def empty() {
        render [:] as JSON
    }
}

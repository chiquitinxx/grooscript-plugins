package websockets

import geb.MyGebTests

class WebsocketsSpec extends MyGebTests {

    void "normal websocket messages"() {
        when:
        go '/websockets'

        then:
        waitFor {
            $('#first').text() == 'hello from controller, "World"!'
        }
    }
}
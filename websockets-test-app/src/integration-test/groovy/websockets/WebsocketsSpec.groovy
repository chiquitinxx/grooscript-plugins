package websockets

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback

@Integration
@Rollback
class WebsocketsSpec extends GebSpec {

    void "normal websocket messages"() {
        when:
        go '/websockets'

        then:
        waitFor {
            $('#first').text() == 'hello from controller, "World"!'
        }
    }
}
package events

import geb.MyGebTests

class FirstEventsSpec extends MyGebTests {

    void "first test grails events"() {
        when:
        go '/firstEvents'

        then:
        waitFor {
            println $('body').text()
            $('#first').text() == 'Ok'
            $('#second').text() == 'hello from service!'
            $('#third').text() == '1.23'
            $('#fourth').text() == '3'
        }
    }
}
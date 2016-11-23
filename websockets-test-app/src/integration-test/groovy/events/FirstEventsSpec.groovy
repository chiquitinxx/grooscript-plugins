package events

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback

@Integration
@Rollback
class FirstEventsSpec extends GebSpec {

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
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
            $('#first').text() == 'Ok'
            $('#second').text() == 'hello from service!'
            $('#third').text() == '[a: a ,b: 1.23 ,c: 1348230211000 ,]'
            $('#fourth').text() == '1'
        }
    }
}
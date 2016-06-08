package javascript

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback

@Integration
@Rollback
class ResponsesSpec extends GebSpec {

    void "check ajax remote calls"() {
        when:
        go '/responses'

        then:
        waitFor {
            $('body').text() == '''Hello Wold! -> 1
Hello in text!
isEmpty'''
        }
    }
}

package tags

import grails.test.mixin.integration.Integration
import grails.transaction.*
import geb.spock.*

@Integration
@Rollback
class TagCodeSpec extends GebSpec {

    void "check grooscript:code works"() {
        when:
        go '/tagCode'

        then:
        waitFor {
            $('body').text() == 'Hello World! from grooscript :)'
        }
    }
}

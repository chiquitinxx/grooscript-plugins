package tags

import grails.test.mixin.integration.Integration
import grails.transaction.*
import geb.spock.*

@Integration
@Rollback
class ConvertSpec extends GebSpec {

    void "check grooscript:code works in tagConvert index"() {
        when:
        go '/tagConvert'

        then:
        $('body').text() == 'Hello World! from grooscript :)'
    }
}

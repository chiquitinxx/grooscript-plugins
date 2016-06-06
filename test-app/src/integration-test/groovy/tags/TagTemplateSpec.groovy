package tags

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback

@Integration
@Rollback
class TagTemplateSpec extends GebSpec {

    void "check grooscript:template works"() {
        when:
        go '/tagTemplate'

        then:
        $('#first').text() == '''0 li item
1 li item
2 li item
3 li item
4 li item'''
    }
}
package tags

import geb.MyGebTests

class TagTemplateSpec extends MyGebTests {

    void "check grooscript:template works"() {
        when:
        go '/tagTemplate'

        then:
        waitFor {
            $('#first').text() == '''0 li item
1 li item
2 li item
3 li item
4 li item'''
        }
    }
}
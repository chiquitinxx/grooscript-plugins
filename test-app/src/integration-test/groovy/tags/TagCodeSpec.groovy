package tags

import geb.MyGebTests

class TagCodeSpec extends MyGebTests {

    void "check grooscript:code works"() {
        when:
        go '/tagCode'

        then:
        waitFor {
            $('body').text() == 'Hello World! from grooscript :)'
        }
    }
}

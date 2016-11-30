package javascript

import geb.MyGebTests

class ResponsesSpec extends MyGebTests {

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

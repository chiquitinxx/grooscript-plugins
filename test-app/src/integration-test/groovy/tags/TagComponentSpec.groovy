package tags

import geb.MyGebTests

class TagComponentSpec extends MyGebTests {

    void "check grooscript:code works"() {
        when:
        go '/tagComponent'

        then:
        waitFor {
            $('body').text() == '''Hello World! - 12
Hello from grooscript! - 22
Hello World! - 12
Hello World! - 12'''
        }
    }
}

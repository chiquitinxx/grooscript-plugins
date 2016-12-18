package tags

import geb.MyGebTests
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty("geb.saucelabs.browser") })
class TagComponentSpec extends MyGebTests {

    void "check components work"() {
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

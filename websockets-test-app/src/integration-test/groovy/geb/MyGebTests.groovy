package geb

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import io.github.bonigarcia.wdm.FirefoxDriverManager

@Integration
class MyGebTests extends GebSpec {

    def setup() {
        if (System.getProperty("geb.saucelabs.browser")) {
            browser.config.cacheDriverPerThread = false
        } else {
            FirefoxDriverManager.getInstance().setup()
        }
    }
}

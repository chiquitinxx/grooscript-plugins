package geb

import geb.driver.CachingDriverFactory
import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import io.github.bonigarcia.wdm.ChromeDriverManager

@Integration
class MyGebTests extends GebSpec {

    def setup() {
        if (System.getProperty("geb.saucelabs.browser")) {
            browser.config.cacheDriverPerThread = false
        } else {
            ChromeDriverManager.getInstance().setup()
        }
    }

    def cleanup() {
        CachingDriverFactory.clearCache()
    }
}

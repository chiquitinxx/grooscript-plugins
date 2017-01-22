import geb.driver.SauceLabsDriverFactory

def sauceLabsBrowser = System.getProperty("geb.saucelabs.browser")
if (sauceLabsBrowser) {
    driver = {
        def username = System.getenv("GEB_SAUCE_LABS_USER")
        assert username
        def accessKey = System.getenv("GEB_SAUCE_LABS_ACCESS_PASSWORD")
        assert accessKey
        new SauceLabsDriverFactory().create(sauceLabsBrowser, username, accessKey)
    }
} else {
    driver = "chrome"
}

waiting {
    if (sauceLabsBrowser) {
        timeout = 30
        retryInterval = 1

    } else {
        timeout = 10
        retryInterval = 0.5
    }
}
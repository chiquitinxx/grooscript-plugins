package org.grooscript.gradle.websocket

import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 16/12/14.
 */
class ClientSpec extends Specification {

    def 'send a message'() {
        when:
        Client.connectAndSend 'http://localhost:8080/hello', '/app/hello', [name: 'Yilas']
        sleep(1000)

        then:
        noExceptionThrown()
    }
}

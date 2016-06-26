package org.grooscript.grails.event

import groovy.transform.ToString

@ToString
class WsMessage {
    String name
    Object data
}

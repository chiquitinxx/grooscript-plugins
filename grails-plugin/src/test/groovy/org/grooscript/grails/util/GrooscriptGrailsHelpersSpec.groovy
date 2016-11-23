package org.grooscript.grails.util

import grails.core.GrailsApplication
import grails.web.mapping.LinkGenerator
import org.springframework.context.ApplicationContext
import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 23/11/16.
 */
class GrooscriptGrailsHelpersSpec extends Specification {

    void 'get destination prefix from bean'() {
        given:
        applicationContext.getBean('grailsSimpAnnotationMethodMessageHandler') >> [
                destinationPrefixes: ['/app/']
        ]

        expect:
        grailsHelpers.websocketDestinationPrefix == '/app'
    }

    private ApplicationContext applicationContext = Stub(ApplicationContext)
    private GrooscriptGrailsHelpers grailsHelpers = new GrooscriptGrailsHelpers(Stub(LinkGenerator),
            Stub(JavascriptTemplate), Stub(GrailsApplication), applicationContext)
}

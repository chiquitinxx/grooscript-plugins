package org.grooscript.grails.util

import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 11/06/14
 */
class GrooscriptTemplateSpec extends Specification {

    void 'apply template without binding'() {
        expect:
        grooscriptTemplate.apply(ANY_TEMPLATE) == ANY_TEMPLATE
    }

    void 'apply template with binding'() {
        given:
        def binding = [name: "Jorge"]

        expect:
        grooscriptTemplate.apply(NAME_TEMPLATE, binding) == 'Hello Jorge!'
    }

    void 'apply template with $ char'() {
        expect:
        grooscriptTemplate.apply(UGLY_TEMPLATE) == 'any $ template'
    }

    static final ANY_TEMPLATE = 'any template'
    static final NAME_TEMPLATE = 'Hello ${name}!'
    static final UGLY_TEMPLATE = 'any \\$ template'

    GrooscriptTemplate grooscriptTemplate = new GrooscriptTemplate()
}

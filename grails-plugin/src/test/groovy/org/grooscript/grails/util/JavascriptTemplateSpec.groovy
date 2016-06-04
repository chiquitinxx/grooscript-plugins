package org.grooscript.grails.util

import spock.lang.Specification

class JavascriptTemplateSpec extends Specification {

    void 'apply template without binding'() {
        expect:
        template.apply(ANY_TEMPLATE) == ANY_TEMPLATE
    }

    void 'apply template with binding'() {
        given:
        def binding = [name: "Jorge"]

        expect:
        template.apply(NAME_TEMPLATE, binding) == 'Hello Jorge!'
    }

    void 'apply template with $ char'() {
        expect:
        template.apply(UGLY_TEMPLATE) == 'any $ template'
    }

    static final ANY_TEMPLATE = 'any template'
    static final NAME_TEMPLATE = 'Hello ${name}!'
    static final UGLY_TEMPLATE = 'any \\$ template'

    JavascriptTemplate template = new JavascriptTemplate()
}

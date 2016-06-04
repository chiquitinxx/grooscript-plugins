package org.grooscript.grails.core.converter

import spock.lang.Specification

class CoreConverterSpec extends Specification {

    def 'convert component'() {
        when:
        def result = converter.convertComponent('class MyComponent { def render() {} }', [:])

        then:
        result.contains 'gSobject.cId = null'
    }

    private Converter converter = new CoreConverter()
}

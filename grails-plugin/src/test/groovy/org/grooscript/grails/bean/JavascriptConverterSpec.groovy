package org.grooscript.grails.bean

import org.grooscript.grails.core.Conversion
import org.grooscript.grails.util.Util
import spock.lang.Specification

class JavascriptConverterSpec extends Specification {

    def 'convert to javascript without conversion options'() {
        given:
        conversion.convertToJavascript(groovyCode, [:]) >> jsCode

        when:
        def result = javascriptConverter.toJavascript(groovyCode, null)

        then:
        result == jsCode
    }

    def 'convert a remote model domain class with source code available'() {
        given:
        javascriptConverter.sourceCodeAvailable = true
        conversion.convertRemoteDomainClass(fullDomainClassName) >> jsCode

        when:
        String result = javascriptConverter.convertRemoteDomainClass(fullDomainClassName)

        then:
        result == jsCode
    }

    def 'convert a remote model domain class without source code available'() {
        given:
        GroovySpy(Util, global: true)
        javascriptConverter.sourceCodeAvailable = false

        when:
        String result = javascriptConverter.convertRemoteDomainClass(fullDomainClassName)

        then:
        1 * Util.getResourceText(shortDomainClassName + '.grs') >> jsCode
        result == jsCode
    }

    def 'get component converted code with source code available'() {
        given:
        javascriptConverter.sourceCodeAvailable = true
        conversion.convertComponent(fullClassName, nameComponent) >> jsCode

        when:
        String result = javascriptConverter.getComponentCodeConverted(fullClassName, shortClassName, nameComponent)

        then:
        result == jsCode
    }

    def 'get component converted code without source code available'() {
        given:
        GroovySpy(Util, global: true)
        javascriptConverter.sourceCodeAvailable = false

        when:
        String result = javascriptConverter.getComponentCodeConverted(fullClassName, shortClassName, nameComponent)

        then:
        1 * Util.getResourceText(shortClassName + '.gcs') >> jsCode
        result == jsCode
    }

    private final String shortDomainClassName = 'Short'
    private final String fullDomainClassName = "package.${shortDomainClassName}"
    private final String fullClassName = 'fullClassName'
    private final String shortClassName = 'shortClassName'
    private final String nameComponent = 'nameComponent'
    private final String groovyCode = 'any groovy code'
    private final String jsCode = 'any js code'
    private Conversion conversion = Stub(Conversion)
    private JavascriptConverter javascriptConverter = new JavascriptConverter()

    def setup() {
        javascriptConverter.grailsConversions = conversion
    }
}

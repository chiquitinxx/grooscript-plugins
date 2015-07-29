package org.grooscript.grails.bean

import org.grooscript.GrooScript
import org.grooscript.grails.remote.RemoteDomainClass
import org.grooscript.grails.util.Util
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 05/06/14
 */
class GrooscriptConverterSpec extends Specification {

    def 'convert to javascript without conversion options'() {
        given:
        def code = CODE

        when:
        def result = grooscriptConverter.toJavascript(code, null)

        then:
        1 * GrooScript.convert(CODE, [
                classPath: ['src/main/groovy'],
                mainContextScope: GrooscriptConverter.DEFAULT_CONVERSION_SCOPE_VARS
        ])
        result == 'var a = 5;\ngs.mc(b,"go",[]);\n'
    }

    def 'convert a remote model domain class'() {
        given:
        GroovySpy(Util, global: true)
        def conversionOptions = [one: 1]

        when:
        def result = grooscriptConverter.convertRemoteDomainClass(DOMAIN_CLASS)

        then:
        1 * Util.getDomainFileText(DOMAIN_CLASS) >> CODE
        1 * Util.customizationAstOption(RemoteDomainClass) >> conversionOptions
        1 * GrooScript.convert(CODE, conversionOptions) >> JS
        result == JS
    }

    private static final JS = 'var a = 5; b.go()'
    private static final CODE = 'def a = 5; b.go()'
    private static final DOMAIN_CLASS = 'domainClass'
    def grooscriptConverter = new GrooscriptConverter()

    def setup() {
        GroovySpy(GrooScript, global: true)
    }
}

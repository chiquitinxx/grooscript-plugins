package org.grooscript.grails.bean

import org.grooscript.GrooScript
import org.grooscript.grails.core.Conversion
import org.grooscript.grails.remote.RemoteDomainClass
import org.grooscript.grails.util.Util
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 05/06/14
 */
class GrooscriptConverterSpec extends Specification {

    def 'convert to javascript without conversion options'() {
        when:
        def result = grooscriptConverter.toJavascript(groovyCode, null)

        then:
        1 * GrooScript.convert(groovyCode, [
                classpath: ['src/main/groovy'],
                mainContextScope: ['$', 'gsEvents', 'window', 'document', 'HtmlBuilder', 'GQueryImpl',
                                   'TODO','Observable', 'ClientEventHandler', 'GrooscriptGrails']
        ]) >> jsCode
        result == jsCode
    }

    def 'convert a remote model domain class'() {
        given:
        GroovySpy(Util, global: true)
        def conversionOptions = [one: 1]

        when:
        def result = grooscriptConverter.convertRemoteDomainClass(DOMAIN_CLASS)

        then:
        1 * Util.getDomainFileText(DOMAIN_CLASS) >> groovyCode
        1 * Util.customizationAstOption(RemoteDomainClass) >> conversionOptions
        1 * GrooScript.convert(groovyCode, conversionOptions) >> jsCode
        result == jsCode
    }

    def 'convert templates'() {
        when:
        def result = grooscriptConverter.toJavascript('''def gsTextHtml = { data -> HtmlBuilder.build { builderIt ->
    [1, 2].each { println it }
}}''', [mainContextScope: ['HtmlBuilder']])
        then:
        result == '''var gsTextHtml = function(data) {
  return gs.mc(HtmlBuilder,"build",[function(builderIt) {
    return gs.mc(gs.list([1 , 2]),"each",[function(it) {
      return gs.println(it);
    }]);
  }]);
};
'''
    }

    def 'get component converted code with source code available'() {
        given:
        GroovySpy(Util, global: true)
        grooscriptConverter.sourceCodeAvailable = true
        conversion.convertComponent(groovyCode, shortClassName, nameComponent) >> jsCode

        when:
        String result = grooscriptConverter.getComponentCodeConverted(fullClassName, shortClassName, nameComponent)

        then:
        1 * Util.getClassSource(fullClassName) >> groovyCode
        result == jsCode
    }

    def 'get component converted code without source code available'() {
        given:
        GroovySpy(Util, global: true)
        grooscriptConverter.sourceCodeAvailable = false

        when:
        String result = grooscriptConverter.getComponentCodeConverted(fullClassName, shortClassName, nameComponent)

        then:
        1 * Util.getResourceText(shortClassName + '.cs') >> jsCode
        result == jsCode
    }

    private static final DOMAIN_CLASS = 'domainClass'
    private final String fullClassName = 'fullClassName'
    private final String shortClassName = 'shortClassName'
    private final String nameComponent = 'nameComponent'
    private final String groovyCode = 'any groovy code'
    private final String jsCode = 'any js code'
    private Conversion conversion = Stub(Conversion)
    private GrooscriptConverter grooscriptConverter = new GrooscriptConverter()

    def setup() {
        GroovySpy(GrooScript, global: true)
        grooscriptConverter.grailsConversions = conversion
    }
}

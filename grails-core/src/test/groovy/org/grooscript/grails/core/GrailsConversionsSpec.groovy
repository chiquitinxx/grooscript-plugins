package org.grooscript.grails.core

import org.grooscript.grails.core.converter.Converter
import spock.lang.Specification

class GrailsConversionsSpec extends Specification {

    void 'convert grails component'() {
        given:
        converter.convertComponent(code, [
                classpath: 'src/main/groovy',
                includeDependencies: true,
                mainContextScope:['$', 'gsEvents', 'window', 'document', 'HtmlBuilder',
                                  'GsHlp', 'GQueryImpl', 'Observable', 'ClientEventHandler',
                                  'GrooscriptGrails']
        ]) >> convertedCode

        when:
        String result = conversions.convertComponent(code, className, nameComponent)

        then:
        result == "${convertedCode};GrooscriptGrails.createComponent(${className}, '${nameComponent}');"
    }

    private String code = 'code'
    private String className = 'className'
    private String nameComponent = 'nameComponent'
    private String convertedCode = 'convertedCode'
    private Converter converter = Stub(Converter)
    private GrailsConversions conversions

    def setup() {
        conversions = new GrailsConversions()
        conversions.converter = converter
    }
}

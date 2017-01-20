package org.grooscript.grails.core

import org.grooscript.grails.core.converter.Converter
import org.grooscript.grails.core.util.FileSupport
import spock.lang.Ignore
import spock.lang.Specification

import static org.grooscript.grails.core.GrailsConversions.*

class GrailsConversionsSpec extends Specification {

    void 'convert to javascript'() {
        given:
        converter.convert(code, [
                classpath: [GROOVY_SRC_DIR],
                includeDependencies: true,
                mainContextScope: DEFAULT_CONVERSION_SCOPE_VARS
        ]) >> convertedCode

        when:
        String result = conversions.convertToJavascript(code, [:])

        then:
        result == convertedCode
    }

    void 'convert grails component'() {
        given:
        converter.convertComponent(code, [
                classpath: [GROOVY_SRC_DIR],
                includeDependencies: true,
                mainContextScope: DEFAULT_CONVERSION_SCOPE_VARS
        ]) >> convertedCode

        when:
        String result = conversions.convertComponent(fullClassName, nameComponent)

        then:
        1 * fileSupport.getFileContent("src${SEP}main${SEP}groovy${SEP}package${SEP}${shortClassName}.groovy") >> code
        result == "${convertedCode};GrooscriptGrails.createComponent(${shortClassName}, '${nameComponent}');"
    }

    private String fullClassName = 'package.Plass'
    private String shortClassName = 'Plass'
    private String code = 'code'
    private String nameComponent = 'nameComponent'
    private String convertedCode = 'convertedCode'
    private Converter converter = Stub(Converter)
    private FileSupport fileSupport = Mock(FileSupport)
    private GrailsConversions conversions

    def setup() {
        conversions = new GrailsConversions()
        conversions.converter = converter
        conversions.fileSupport = fileSupport
    }
}

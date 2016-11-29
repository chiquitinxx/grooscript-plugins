package core

import org.grooscript.grails.core.Conversion
import org.grooscript.grails.core.GrailsConversions
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 29/11/16.
 */
class GrailsConversionsSpec extends Specification {

    void 'convert a remote domain class'() {
        when:
        String jsCode = grailsConversions.convertRemoteDomainClass('test.Book')
        File file = testProjectDir.newFile()

        then:
        jsCode

        when:
        def (result, errorMessage) = grailsConversions.saveConversionForPackaging(
                testProjectDir.getRoot(), file.name, jsCode)

        then:
        result
        !errorMessage
        file.text == jsCode
    }

    private Conversion grailsConversions = new GrailsConversions()
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
}

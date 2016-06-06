package test

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(TagCodeController)
class TagConvertControllerSpec extends Specification {

    void "index"() {
        when:
        controller.index()

        then:
        response.status == 200
    }
}

package domain

import grails.test.mixin.integration.Integration
import grails.transaction.*
import org.grooscript.grails.bean.JavascriptConverter
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.*

@Integration
@Rollback
class RemoteDomainSpec extends Specification {

    void 'convert domain class'() {
        when:
        String code = javascriptConverter.convertRemoteDomainClass('test.Book')

        then:
        code.contains('Book.url = "/books";')
    }

    @Autowired
    private JavascriptConverter javascriptConverter
}

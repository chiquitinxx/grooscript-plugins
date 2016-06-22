package resources

import grails.test.mixin.integration.Integration
import grails.web.mime.MimeType
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Integration
@Stepwise
class BooksSpec extends Specification {

    @Shared
    def rest = new RESTClient('http://localhost:8080/')
    @Shared
    def idSaved

    void "get initial list of books"() {
        when:
        def resp = rest.get(path: 'books')

        then:
        resp.status == HttpStatus.OK.value()
        resp.contentType == MimeType.JSON.name
        resp.data.size() == 0
    }

    void "insert a book"() {
        when:
        def resp = rest.post(path: 'books',
                             body: bookToInsert,
                requestContentType: ContentType.JSON)
        idSaved = resp.data.id

        then:
        resp.status == HttpStatus.CREATED.value()
        resp.contentType == 'application/json'
        resp.data == initialSavedBook
    }

    void 'retrieve inserted book'() {
        when:
        def resp = rest.get(path: 'books')

        then:
        resp.status == HttpStatus.OK.value()
        resp.contentType == MimeType.JSON.name
        resp.data.size() == 1

        and:
        resp.data[0] == initialSavedBook
    }

    void 'update title'() {
        when:
        def resp = rest.put(path: "books/${idSaved}",
                body: toUpdateBook,
                requestContentType: ContentType.JSON)

        then:
        resp.status == HttpStatus.OK.value()
        resp.contentType == MimeType.JSON.name

        and:
        resp.data == toUpdateBook

        when:
        def getResp = rest.get(path: "books/${idSaved}")

        then:
        getResp.status == HttpStatus.OK.value()
        getResp.contentType == MimeType.JSON.name
        getResp.data == toUpdateBook
    }

    void 'failing update number pages'() {
        when:
        rest.put(path: "books/${idSaved}",
            body: toUpdateBook << [pages: 'a number?'],
            requestContentType: ContentType.JSON)

        then:
        def e = thrown(HttpResponseException)
        e.statusCode == 422
    }

    void 'delete a book'() {
        when:
        def resp = rest.delete(path: "books/${idSaved}")

        then:
        resp.status == HttpStatus.NO_CONTENT.value()

        when:
        def newResp = rest.get(path: 'books')

        then:
        newResp.status == HttpStatus.OK.value()
        newResp.contentType == MimeType.JSON.name
        newResp.data.size() == 0
    }

    private Map bookToInsert = [author: 'Any author', pages: 123, title: 'Book title']
    private Map getInitialSavedBook() {
        [author: 'Any author', id: idSaved, pages: 123, title: 'Book title']
    }
    private Map toUpdateBook = [author: 'Any author', id: idSaved, pages: 121, title: 'New Title']
}

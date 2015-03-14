package test

import grails.rest.Resource

@Resource(uri='/books', formats=['json'])
class Book {

    String title

    static constraints = {
    }

    private String upperTitle() {
        title.toUpperCase()
    }
}

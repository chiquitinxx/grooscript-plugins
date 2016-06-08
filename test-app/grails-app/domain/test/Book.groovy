package test

import grails.rest.Resource

@Resource(uri = "/books", formats = ["json"])
class Book {

    String title
    String author
    Integer pages

    static constraints = {
    }
}

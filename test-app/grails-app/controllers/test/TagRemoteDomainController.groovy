package test

class TagRemoteDomainController {

    def index() {
        def list = Book.list()
        list?.each {
            it.delete(flush: true)
        }
        render view: 'index'
    }
}

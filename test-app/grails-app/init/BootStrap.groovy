import test.Book

class BootStrap {

    def init = { servletContext ->
        Book.executeUpdate("delete Book b where b.id > 0")
    }
    def destroy = {
    }
}

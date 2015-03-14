package test

class Actor {

    String name
    Date birth
    int oscars = 0

    static constraints = {
        name blank: false
    }

    boolean goodActor() {
        oscars > 0
    }
}

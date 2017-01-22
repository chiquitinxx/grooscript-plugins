package component

class Message {

    String message = 'Hello World!'

    def render() {
        p "$message - ${message.size()}"
    }
}

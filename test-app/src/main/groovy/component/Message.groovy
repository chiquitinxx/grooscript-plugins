package component

/**
 * Created by jorgefrancoleza on 29/11/16.
 */
class Message {

    String message = 'Hello World!'

    def render() {
        p "$message - ${message.size()}"
    }
}

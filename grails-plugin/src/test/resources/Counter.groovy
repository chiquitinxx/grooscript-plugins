class Counter {
    static style = '''
        button {
            background-color: red;
        }
    '''
    static renderAfter = ['inc', 'dec']
    int value = 0
    void inc() {
        value++
    }
    void dec() {
        value--
    }
    def render() {
        h1 value.toString()
        p {
            button(onclick: 'dec', '-')
            button(onclick: 'inc', '+')
        }
    }
}
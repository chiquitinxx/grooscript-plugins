class Counter {
    static style = '''
        button {
            background-color: red;
        }
    '''
    def value = 0
    def inc() {
        value++
        draw()
    }
    def dec() {
        value--
        draw()
    }
    def draw() {
        h1 value.toString()
        p {
            button(onclick: 'dec', '-')
            button(onclick: 'inc', '+')
        }
    }
}
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
            button(onclick: "GrooscriptGrails.recover(${cId}).dec(this)", '-')
            button(onclick: "GrooscriptGrails.recover(${cId}).inc(this)", '+')
        }
    }
}
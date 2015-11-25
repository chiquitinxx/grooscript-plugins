package org.grooscript.grails.component

/**
 * Created by jorgefrancoleza on 25/11/15.
 */
class ComponentTest extends GroovyTestCase {

    void testComponentAstWorks() {
        assertScript basicComponent + '''
            assert component.metaClass.properties.find { it.name == 'shadowRoot' }
'''
    }

    void testThrowErrorIfNoDrawMethod() {
        try {
            assertScript '''
        import org.grooscript.grails.component.Component

        @Component
        class MyComponent {}
'''
            assert false
        } catch (RuntimeException e) {
            assert e.message.startsWith('startup failed:\nGeneral error during semantic analysis:' +
                    ' You have to define a draw method')
        }
    }

    void testDrawFunction() {
        assertScript basicComponent + '''
            component.shadowRoot = new Expando()
            component.draw()
            assert component.shadowRoot.innerHTML == '<p>hello!</p>'
'''
    }

    void testDrawFunctionWithStyle() {
        assertScript styleComponent + '''
            component.shadowRoot = new Expando()
            component.draw()
            assert component.shadowRoot.innerHTML == '<style>anyStyle</style><p>hello!</p>'
'''
    }

    private final String basicComponent = '''
        import org.grooscript.grails.component.Component

        @Component
        class MyComponent {
            def draw() {
                p 'hello!'
            }
        }

        def component = new MyComponent()
        '''

    private final String styleComponent = '''
        import org.grooscript.grails.component.Component

        @Component
        class MyComponent {
            static style = 'anyStyle'
            def draw() {
                p 'hello!'
            }
        }

        def component = new MyComponent()
        '''
}

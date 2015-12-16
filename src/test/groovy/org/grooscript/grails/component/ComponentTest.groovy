package org.grooscript.grails.component

/**
 * Created by jorgefrancoleza on 25/11/15.
 */
class ComponentTest extends GroovyTestCase {

    void testComponentAstWorks() {
        assertScript basicComponent + '''
            assert component.metaClass.properties.find { it.name == 'shadowRoot' }
            assert component.metaClass.properties.find { it.name == 'cId' }
'''
    }

    void testThrowErrorIfNoRenderMethod() {
        try {
            assertScript '''
        import org.grooscript.grails.component.Component

        @Component
        class MyComponent {}
'''
            assert false
        } catch (RuntimeException e) {
            assert e.message.startsWith('startup failed:\nGeneral error during semantic analysis:' +
                    ' You have to define a render method')
        }
    }

    void testRenderFunction() {
        assertScript basicComponent + '''
            component.shadowRoot = new Expando()
            component.render()
            assert component.shadowRoot.innerHTML == '<p>hello!</p>'
'''
    }

    void testRenderFunctionWithStyle() {
        assertScript styleComponent + '''
            component.shadowRoot = new Expando()
            component.render()
            assert component.shadowRoot.innerHTML == '<style>anyStyle</style><p>hello!</p>'
'''
    }

    void testOnMethodSubstitution() {
        assertScript onComponentWithOnClickMethod("'click'")
        assertScript onComponentWithOnClickMethod('\"click\"')
    }

    void testRenderAfterMethod() {
        assertScript renderAfterScriptWithValue("['click']")
        assertScript renderAfterScriptWithValue("'click'")
        assertScript renderAfterScriptWithValue("\"click\"")
    }

    private final String basicComponent = '''
        import org.grooscript.grails.component.Component

        @Component
        class MyComponent {
            def render() {
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
            def render() {
                p 'hello!'
            }
        }

        def component = new MyComponent()
        '''

    private final String renderAfterScriptWithValue(String value) {
        """
        import org.grooscript.grails.component.Component

        @Component
        class MyComponent {
            static renderAfter = ${value}
            def value = 0

            def click() {
                value = 1
            }
            def render() {
                p 'hello!' + value
            }
        }

        def component = new MyComponent()
        component.shadowRoot = new Expando()
        component.click()
        assert component.shadowRoot.innerHTML == "<p>hello!1</p>"
        """
    }

    private String onComponentWithOnClickMethod(String method) {
        """
        import org.grooscript.grails.component.Component

        @Component
        class MyComponent {
            def click() {
                'clicked!'
            }
            def render() {
                p(onclick: ${method}, 'hello!')
            }
        }

        def component = new MyComponent()
        component.shadowRoot = new Expando()
        component.cId = 5
        component.render()
        assert component.shadowRoot.innerHTML == "<p onclick='GrooscriptGrails.recover(5).click(this)'>hello!</p>"
        """
    }
}

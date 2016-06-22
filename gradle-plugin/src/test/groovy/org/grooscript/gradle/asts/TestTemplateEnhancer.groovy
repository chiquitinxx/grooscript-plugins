package org.grooscript.gradle.asts

import org.grooscript.GrooScript

/**
 * User: jorgefrancoleza
 * Date: 09/10/14
 */
class TestTemplateEnhancer extends GroovyTestCase {

    void testApplyEnhancerToChangeInclude() {
        assertScript CODE
        String converted = GrooScript.convert(CODE)
        assert converted.contains('yieldUnescaped')
        assert converted.contains("Templates,'applyTemplate'")
        //println converted
    }

    static final CODE = '''
import org.grooscript.gradle.asts.TemplateEnhancer

@TemplateEnhancer
class Tem {
    static templates = ['hello': { model ->
        include template: 'one.gtpl'
    }]
}

assert new Tem()
'''
}

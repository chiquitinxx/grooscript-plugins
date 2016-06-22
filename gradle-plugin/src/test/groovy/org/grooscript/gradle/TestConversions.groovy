package org.grooscript.gradle

import org.grooscript.GrooScript

/**
 * User: jorgefrancoleza
 * Date: 25/09/14
 */
class TestConversions extends GroovyTestCase {

    void testConvertTrait() {
        def result = GrooScript.convert '''
    import org.grooscript.jquery.*

    trait Jquery {
        GQuery gquery = new GQueryImpl()
    }

    class A implements Jquery {}
'''
        assert result.contains('Jquery.$init$(gSobject);')
    }
}

package org.grooscript.grails.remote

/**
 * User: jorgefrancoleza
 * Date: 23/06/14
 */
class TestRemoteDomainAst extends GroovyTestCase {

    void testAstRemoteDomain() {
        assertScript '''
            @org.grooscript.grails.remote.RemoteDomainClass
            class Book {
                String name
                String title
            }
            assert Book.metaClass.methods.find { it.name == 'save'}
            assert Book.class.simpleName == 'Book'
            assert Book.gsName == 'Book'
        '''
    }
}

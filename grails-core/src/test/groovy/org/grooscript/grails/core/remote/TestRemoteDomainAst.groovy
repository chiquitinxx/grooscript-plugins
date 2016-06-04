package org.grooscript.grails.core.remote

class TestRemoteDomainAst extends GroovyTestCase {

    void testAstRemoteDomain() {
        assertScript '''
            @org.grooscript.grails.core.remote.RemoteDomainClass
            class Book {
                String name
                String title
            }
            assert Book.metaClass.methods.find { it.name == 'save'}
            assert Book.class.simpleName == 'Book'
            assert Book.gsName == 'Book'
            assert Book.declaredMethods.findAll { !it.synthetic }.find { it.name == 'list'}
        '''
    }
}

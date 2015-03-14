package org.grooscript.grails.domain

import org.grooscript.GrooScript

/**
 * User: jorgefrancoleza
 * Date: 05/06/14
 */
class DomainClassSpec extends GroovyTestCase {

    private static final NAME = 'name'
    private static final OTHER_NAME = 'other_name'
    private static final VALUE = 'value'
    private static final FAKE_ID = -3464356

    @DomainClass class AstItem {
        String name
        Integer number

        static constraints = {
            name example:true
        }

        boolean equals(Object o) {
            o instanceof AstItem && this.name == o.name && this.number == o.number
        }
    }

    @DomainClass class AstItemWithBlankValidation {
        String name
        Integer number

        static constraints = {
            name blank:false
        }
    }

    void setUp() {
        AstItem.lastId = 0
        AstItem.listItems = []
        GrooScript.clearAllOptions()
    }

    //Not working in grails 2.4
    void testAnnotation() {
        assertScript '''
            @org.grooscript.grails.domain.DomainClass
            class Guy {
                def name
            }

            def guy = new Guy(name: 'Jorge')
            assert guy.name == 'Jorge'
'''
    }

    //Not working in grails 2.4
    void testPropertiesAdded() {
        assertScript '''
            import groovy.transform.ASTTest
            import org.codehaus.groovy.ast.*
            import org.codehaus.groovy.ast.expr.ListExpression
            import static org.codehaus.groovy.ast.ClassHelper.*
            import static org.codehaus.groovy.control.CompilePhase.*

            @org.grooscript.grails.domain.DomainClass
            @ASTTest(phase=SEMANTIC_ANALYSIS, value={
                assert node instanceof ClassNode
                def id = node.getDeclaredField('id')
                assert id instanceof FieldNode
                assert id.type == Long_TYPE
                def listItems = node.getDeclaredField('listItems')
                assert listItems instanceof FieldNode
                assert listItems.static
                def initialExpr = listItems.initialExpression
                assert initialExpr instanceof ListExpression
            })
            class Guy {
                def name
            }

            assert !new Guy().id
'''
    }

    void testDomainClassProperties() {
        def item = new AstItem()

        assert AstItem.listItems == []
        assert AstItem.lastId == 0
        assert AstItem.listColumns.size() == 2
        assert AstItem.listColumns.find{it.name==NAME}.name == NAME
        assert AstItem.listColumns.find{it.name==NAME}.type == 'java.lang.String'
        assert AstItem.listColumns.find{it.name==NAME}.constraints == [example:true]
        assert AstItem.listColumns.find{it.name=='number'}.name == 'number'
        assert AstItem.listColumns.find{it.name=='number'}.type == 'java.lang.Integer'
        assert AstItem.listColumns.find{it.name=='number'}.constraints == [:]
        assert item.metaClass.methods.find { it.name=='save'}
        assert item.metaClass.methods.find { it.name=='delete'}
    }

    void testGetMethod() {
        def savedItem = basicItem
        assert savedItem.id == 1
        def item = AstItem.get(1)

        assert item.id == 1
        assert !AstItem.get(FAKE_ID)
    }

    void testGetMethodObtainsClonedObject() {
        basicItem
        def item = AstItem.get(1)
        def item2 = AstItem.list()[0]

        item2.name = OTHER_NAME

        assert item.name == NAME
        assert item.id == item2.id
        assert item != item2
    }

    void testCreateNewItem() {

        assert AstItem.count() == 0
        def item = new AstItem()

        assert !item.id
        assert AstItem.lastId == 0

        item."${NAME}" = VALUE
        def result = item.save()

        assert result == true
        assert item.id == 1
        assert AstItem.count() == 1
        assert AstItem.listItems.size() == 1
        assert AstItem.listItems[0]."${NAME}" == VALUE
        assert AstItem.lastId == 1
    }

    void testUpdateItem() {
        def item = basicItem

        assert item.name == NAME

        item.name = VALUE
        item.save()

        assert AstItem.get(item.id).name == VALUE
        assert AstItem.list()[0] == item
    }

    void testDeleteAnItem() {
        AstItem item = getBasicItem()

        assert AstItem.count() == 1

        item.delete()

        assert AstItem.count() == 0
        assert !AstItem.list()
        assert !AstItem.listItems
    }

    void testChangeListenerExecuted() {
        def item = new AstItem()
        def value = 15
        item.changeListeners << { it -> println it; value = value * 2}

        item."$NAME" = VALUE
        item.save()

        assert value == 30
    }

    def testBlankValidation() {
        AstItemWithBlankValidation.count() == 0
        AstItemWithBlankValidation item = new AstItemWithBlankValidation()

        assert !item.clientValidations()
        assert !item.validate()

        assert item.hasErrors()
        assert item.errors == [name:'blank validation on value null']

        def result = item.save()

        assert !result
        assert AstItemWithBlankValidation.count() == 0
    }

    private getBasicItem() {
        def item = new AstItem(name: NAME)
        item.save()
        item
    }
}

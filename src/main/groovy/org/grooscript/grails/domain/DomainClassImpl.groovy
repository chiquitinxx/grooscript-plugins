package org.grooscript.grails.domain

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement

/**
 * User: jorgefrancoleza
 * Date: 28/01/13
 */
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import java.lang.reflect.Modifier

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class DomainClassImpl implements ASTTransformation {

    private static final NOT_PROPERTY_NAMES = ['transients', 'constraints', 'mapping', 'hasMany', 'belongsTo']

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {

        if (!nodes[0] instanceof AnnotationNode ||
            !nodes[1] instanceof ClassNode) {
            throw new RuntimeException('Annotation error.')
        }

        ClassNode theClass = (ClassNode) nodes[1]
        //If 'id' property exist we do nothing
        if (theClass.hasProperty('id')) {
            return
        }

        //Get properties for the class and load into listColumns
        ListExpression list = new ListExpression([])
        theClass.properties.each { PropertyNode pn ->
            if (!(pn.name in NOT_PROPERTY_NAMES)) {
                List<MapEntryExpression> listColumnProperties = new ArrayList<MapEntryExpression>()
                listColumnProperties << new MapEntryExpression(new ConstantExpression('name'),new ConstantExpression(pn.name))
                listColumnProperties << new MapEntryExpression(new ConstantExpression('type'),new ConstantExpression(pn.type.name))
                //Let's find constraints
                def constraints = []
                if (theClass.properties.find {it.name=='constraints'}) {
                    try {
                        ClosureExpression clo = theClass.properties.find {it.name=='constraints'}.initialExpression
                        BlockStatement blo = clo.code
                        blo.statements.each { Statement st ->
                            if (st instanceof ExpressionStatement && st.expression instanceof MethodCallExpression) {
                                MethodCallExpression mce = st.expression
                                if (mce.methodAsString==pn.name) {
                                    //We got constraints of this property
                                    NamedArgumentListExpression nameds = mce.arguments.expressions[0]
                                    nameds.mapEntryExpressions.each { MapEntryExpression mpe ->
                                        constraints << mpe
                                    }
                                }
                            }
                        }
                    } catch (e) {
                        constraints = [fail:'fail']
                    }
                }

                listColumnProperties << new MapEntryExpression(new ConstantExpression('constraints'), new MapExpression(constraints))

                def map = new NamedArgumentListExpression(listColumnProperties)
                list.addExpression(new ConstructorCallExpression(new ClassNode(Expando), new TupleExpression(map)))
            }
        }

        //Remove properties not allowed
        theClass.properties.removeAll { it.name in NOT_PROPERTY_NAMES }
        theClass.fields.removeAll { it.name in NOT_PROPERTY_NAMES }

        theClass.addProperty('listColumns', Modifier.STATIC , new ClassNode(ArrayList), list, null, null)
        theClass.addProperty('listItems', Modifier.STATIC , new ClassNode(ArrayList),
                new ListExpression([]), null, null)
        theClass.addProperty('lastId', Modifier.STATIC, ClassHelper.Long_TYPE, new ConstantExpression(0), null, null)

        //Instance variables
        theClass.addProperty('id', Modifier.PUBLIC, ClassHelper.Long_TYPE,null,null,null)
        theClass.addProperty('errors', Modifier.PUBLIC, new ClassNode(HashMap), new MapExpression([]), null, null)
        theClass.addProperty('version', Modifier.PUBLIC, ClassHelper.Long_TYPE,new ConstantExpression(0),null,null)

        theClass.addMethod('clientValidations', Modifier.PUBLIC, ClassHelper.Boolean_TYPE, Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            def result = true
            def item = this
            errors = [:]
            listColumns.each { field ->
                if (field.constraints) {
                    if (field.constraints['blank']==false && !item."${field.name}") {
                        errors.put(field.name,'blank validation on value '+item."${field.name}")
                        result = false
                    }
                }
            }
            return result
        }[0])

        theClass.addMethod('validate',Modifier.PUBLIC,ClassHelper.Boolean_TYPE,Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            return clientValidations()
        }[0])

        //hasErrors()
        theClass.addMethod('hasErrors',Modifier.PUBLIC, ClassHelper.boolean_TYPE,Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {
            return errors
        }[0])

        theClass.addMethod('count', Modifier.STATIC, ClassHelper.int_TYPE, Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            return listItems.size()
        }[0])

        //list
        Parameter[] params = new Parameter[1]
        params[0] = new Parameter(new ClassNode(HashMap), 'params', new ConstantExpression(null))
        theClass.addMethod('list',Modifier.STATIC, new ClassNode(ArrayList), params,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {
            def result = []
            for (domainItem in listItems) {
                def clonedItem = obtainClonedItem(domainItem)
                result.add(clonedItem)
            }
            return result
        }[0])

        //get(id)
        params = new Parameter[1]
        params[0] = new Parameter(ClassHelper.long_TYPE,'value')
        theClass.addMethod('get', Modifier.STATIC, null, params,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            def number = value
            def item = listItems.find { it.id == number}
            return obtainClonedItem(item)
        }[0])

        //obtain(item)
        params = new Parameter[1]
        params[0] = new Parameter(new ClassNode(Object),'item')
        theClass.addMethod('obtainClonedItem', Modifier.STATIC, new ClassNode(Object), params,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            if (item) {
                def newItem = Class.forName(item.class.name).newInstance()
                def copiedItem = item
                listColumns.each { column ->
                    newItem."${column.name}" = copiedItem."${column.name}"
                }
                newItem.id = copiedItem.id
                return newItem
            } else {
                return null
            }
        }[0])

        //Save method
        theClass.addMethod('save', Modifier.PUBLIC, ClassHelper.boolean_TYPE, Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode { //onOk = null, onError = null ->

            if (!this.clientValidations()) {
                return false
            } else {
                //Insert
                if (!this.id) {
                    this.id = ++lastId
                    listItems << this
                    processChanges([action:'insert',item:this])
                } else { //An update
                    //Nothing to do?? :o
                    processChanges([action:'update',item:this])
                }
                return true
            }
        }[0])

        //Delete method
        theClass.addMethod('delete', Modifier.PUBLIC, ClassHelper.Long_TYPE, Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {

            if (this.id) {
                listItems = listItems - this
                processChanges([action:'delete',item:this])
                return this.id
            } else {
                throw new Exception('Deleting not saved object')
            }
        }[0])

        //Change Listeners
        theClass.addProperty('changeListeners', Modifier.STATIC , new ClassNode(ArrayList),
                new ListExpression([]), null, null)

        params = new Parameter[1]
        params[0] = new Parameter(new ClassNode(HashMap),'data')
        theClass.addMethod('processChanges',Modifier.STATIC,null,params,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {
            def actionData = data
            if (changeListeners) {
                changeListeners.each {
                    it.call(actionData)
                }
            }
        }[0])
    }
}
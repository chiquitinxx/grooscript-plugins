package org.grooscript.grails.remote

import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grooscript.grails.promise.RemoteDomain

import java.lang.reflect.Modifier

import static org.grooscript.grails.util.Util.consoleError

/**
 * @author jorge
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class RemoteDomainClassImpl  implements ASTTransformation {

    private static final String RESOURCE_CLASS_NODE = 'grails.rest.Resource'

    @Override
    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        if (!nodes[0] instanceof AnnotationNode || !nodes[1] instanceof ClassNode) {
            throw new RuntimeException('RemoteDomainClassImpl only applies to classes.')
        }

        ClassNode classNode = nodes[1] as ClassNode
        if (classNode.hasProperty('id')) {
            return
        }

        addInstanceProperties(classNode)

        addSaveMethod(classNode)
        addDeleteMethod(classNode)
        addStaticGetMethod(classNode)
        addStaticListMethod(classNode)
    }

    @TypeChecked
    private static void addInstanceProperties(ClassNode classNode) {
        classNode.addProperty('id', Modifier.PUBLIC, ClassHelper.Long_TYPE,null,null,null)
        classNode.addProperty('version', Modifier.PUBLIC, ClassHelper.Long_TYPE,new ConstantExpression(0),null,null)
        classNode.addProperty('url', Modifier.STATIC, ClassHelper.STRING_TYPE,
                new ConstantExpression(getResourceUrl(classNode)),null,null)
        classNode.addProperty('gsName', Modifier.STATIC, ClassHelper.STRING_TYPE,
                new ConstantExpression(classNode.nameWithoutPackage),null,null)
    }

    private void addStaticGetMethod(ClassNode classNode) {
        def params = new Parameter[1]
        params[0] = new Parameter(ClassHelper.long_TYPE, 'value')
        classNode.addMethod('get', Modifier.STATIC, new ClassNode(RemoteDomain), params,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            return new org.grooscript.grails.promise.RemoteDomain(action: 'read',
                    url: url, data: [id: value], name: gsName)
        }[0])
    }

    private void addStaticListMethod(ClassNode classNode) {
        def params = new Parameter[1]
        params[0] = new Parameter(new ClassNode(HashMap), 'params')
        classNode.addMethod('list', Modifier.STATIC, new ClassNode(RemoteDomain), params,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            return new org.grooscript.grails.promise.RemoteDomain(action: 'list',
                    url: url, data: (params ?: [:]), name: gsName)
        }[0])
    }

    private void addSaveMethod(ClassNode classNode) {
        classNode.addMethod('save', Modifier.PUBLIC, new ClassNode(RemoteDomain), Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            def action = (this.id ? 'update' : 'create')
            def props = org.grooscript.grails.util.GrooscriptGrails.getRemoteDomainClassProperties(this)
            return new org.grooscript.grails.promise.RemoteDomain(action: action,
                    url: this.url, data: props, name: this.gsName)
        }[0])
    }

    private void addDeleteMethod(ClassNode classNode) {
        classNode.addMethod('delete', Modifier.PUBLIC, new ClassNode(RemoteDomain), Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            return new org.grooscript.grails.promise.RemoteDomain(action: 'delete',
                    url: this.url,
                    data: [id: this.id], name: this.gsName)
        }[0])
    }

    @TypeChecked
    private static String getResourceUrl(ClassNode classNode) {
        if (classNode.annotations && classNode.annotations.any {
            it.getClassNode().name == RESOURCE_CLASS_NODE }
        ) {
            AnnotationNode annotationNode = classNode.annotations.find { it.classNode.name == RESOURCE_CLASS_NODE }
            def uriParameter = annotationNode.getMember('uri')
            if (!uriParameter) {
                consoleError "Need to define uri parameter in ${classNode.name}"
            }
            uriParameter.text
        } else {
            "/" + classNode.nameWithoutPackage[0].toLowerCase() + classNode.nameWithoutPackage.substring(1)
        }
    }
}

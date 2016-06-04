package org.grooscript.grails.core.remote

import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grooscript.grails.core.promise.RemoteDomain
import org.grooscript.grails.core.util.GrooscriptGrails

import java.lang.reflect.Modifier

import static org.codehaus.groovy.ast.tools.GeneralUtils.*

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class RemoteDomainClassImpl implements ASTTransformation {

    private static final String RESOURCE_CLASS_NODE = 'grails.rest.Resource'
    private static final ClassNode REMOTE_DOMAIN_CLASS_NODE = ClassHelper.makeCached(RemoteDomain)

    @Override
    @TypeChecked
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
        classNode.addProperty('id', Modifier.PUBLIC, ClassHelper.Long_TYPE,
            null, null, null)
        classNode.addProperty('version', Modifier.PUBLIC, ClassHelper.long_TYPE,
            constX(0, true), null, null)
        classNode.addProperty('url', Modifier.STATIC, ClassHelper.STRING_TYPE,
            constX(getResourceUrl(classNode)), null, null)
        classNode.addProperty('gsName', Modifier.STATIC, ClassHelper.STRING_TYPE,
            constX(classNode.nameWithoutPackage), null, null)
    }

    private static void addStaticGetMethod(ClassNode classNode) {
        def params = params(param(ClassHelper.long_TYPE, 'value'))
        def url = param(new ClassNode(String), 'url')
        def value = param(new ClassNode(String), 'value')
        def gsName = param(new ClassNode(String), 'gsName')
        def astNode = new AstBuilder().buildFromCode {
            return new org.grooscript.grails.core.promise.RemoteDomain(action: 'read',
                url: url,
                data: [id: value],
                name: gsName)
        }[0]

        classNode.addMethod('get',
                Modifier.STATIC,
                REMOTE_DOMAIN_CLASS_NODE,
                params,
                ClassNode.EMPTY_ARRAY,
                astNode as Statement)
    }

    private static void addStaticListMethod(ClassNode classNode) {
        def params = params(param(new ClassNode(HashMap), 'params'))
        def url = param(new ClassNode(String), 'url')
        def gsName = param(new ClassNode(String), 'gsName')
        def astNode = new AstBuilder().buildFromCode {
            return new org.grooscript.grails.core.promise.RemoteDomain(action: 'list',
                url: url,
                data: (params ?: [:]),
                name: gsName)
        }[0]

        classNode.addMethod('list',
                Modifier.STATIC,
                REMOTE_DOMAIN_CLASS_NODE,
                params,
                ClassNode.EMPTY_ARRAY,
                astNode as Statement)
    }

    private static void addSaveMethod(ClassNode classNode) {
        def astNode = new AstBuilder().buildFromCode {
            def action = this.id ? 'update' : 'create'
            def props = org.grooscript.grails.core.util.GrooscriptGrails.getRemoteDomainClassProperties(this)
            return new org.grooscript.grails.core.promise.RemoteDomain(action: action,
                url: this.url,
                data: props,
                name: this.gsName)
        }[0]

        classNode.addMethod('save',
            Modifier.PUBLIC,
            REMOTE_DOMAIN_CLASS_NODE,
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            astNode as Statement)
    }

    private static void addDeleteMethod(ClassNode classNode) {
        def astNode = new AstBuilder().buildFromCode {
            return new org.grooscript.grails.core.promise.RemoteDomain(action: 'delete',
                url: this.url,
                data: [id: this.id],
                name: this.gsName)
        }[0]

        classNode.addMethod('delete',
            Modifier.PUBLIC,
            REMOTE_DOMAIN_CLASS_NODE,
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            astNode as Statement)
    }

    @TypeChecked
    private static String getResourceUrl(ClassNode classNode) {
        if (classNode.annotations && classNode.annotations.any {
            it.getClassNode().name == RESOURCE_CLASS_NODE }
        ) {
            AnnotationNode annotationNode = classNode.annotations.find { it.classNode.name == RESOURCE_CLASS_NODE }
            def uriParameter = annotationNode.getMember('uri')
            if (!uriParameter) {
                new RuntimeException("Expected uri parameter in ${classNode.name}.")
            }
            uriParameter.text
        } else {
            "/" + classNode.nameWithoutPackage[0].toLowerCase() + classNode.nameWithoutPackage.substring(1)
        }
    }

}

package org.grooscript.grails.component

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grooscript.builder.HtmlBuilder
import java.lang.reflect.Modifier

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class ComponentImpl implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        //Start
        if (!nodes[0] instanceof AnnotationNode || !nodes[1] instanceof ClassNode) {
            return
        }

        ClassNode classNode = (ClassNode) nodes[1]

        classNode.addProperty('shadowRoot', Modifier.PUBLIC , ClassHelper.OBJECT_TYPE, null, null, null)
        classNode.addProperty('cId', Modifier.PUBLIC , ClassHelper.Number_TYPE, null, null, null)

        HtmlMapVisitor htmlMapVisitor = new HtmlMapVisitor(classNode: classNode)
        classNode.visitContents(htmlMapVisitor)

        manageDrawMethod(classNode)
    }

    private manageDrawMethod(ClassNode classNode) {
        MethodNode drawMethod = classNode.methods.find { it.name == 'draw'}
        if (!drawMethod) {
            throw new RuntimeException("You have to define a draw method")
        } else {
            BlockStatement actualCode = (BlockStatement)drawMethod.code

            VariableScope variableScope = actualCode.getVariableScope()
            VariableScope blockScope = variableScope.copy()

            ClosureExpression closure = new ClosureExpression(Parameter.EMPTY_ARRAY, actualCode)
            VariableScope closureScope = variableScope.copy()
            closure.setVariableScope(closureScope)

            drawMethod.setCode(new BlockStatement([
                new ExpressionStatement(
                    new BinaryExpression(
                        new PropertyExpression(
                            new PropertyExpression(
                                new VariableExpression('this', ClassHelper.OBJECT_TYPE),'shadowRoot'),
                            'innerHTML'
                        ),
                        new Token(Types.ASSIGN, '=', 0, 0),
                        htmlExpression(closure, classNode)
                    )
                )
            ], blockScope))
        }
    }

    private Expression htmlExpression(ClosureExpression closure, ClassNode classNode) {
        if (classHasStyle(classNode)) {
            def staticStyle = new VariableExpression('style')
            new BinaryExpression(
                    new GStringExpression(null, [
                        new ConstantExpression('<style>'),
                        new ConstantExpression('</style>')
                    ], [new VariableExpression(staticStyle)]),
                    new Token(Types.PLUS, '+', 0, 0),
                    buildHtmlExpression(closure)
            )
        } else {
            buildHtmlExpression(closure)
        }
    }

    private boolean classHasStyle(ClassNode classNode) {
        classNode.properties.find {
            it.name == 'style' && it.static
        }
    }

    private Expression buildHtmlExpression(ClosureExpression closure) {
        new MethodCallExpression(
                new ClassExpression(new ClassNode(HtmlBuilder)),
                'build',
                new ArgumentListExpression([
                        closure
                ])
        )
    }
}
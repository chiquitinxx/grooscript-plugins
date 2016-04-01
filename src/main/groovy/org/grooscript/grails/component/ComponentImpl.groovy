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

/**
 * @author Jorge Franco <jorge.franco@osoco.es>
 */
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class ComponentImpl implements ASTTransformation {

    private static final String RENDER_METHOD = 'render'
    private static final String STYLE = 'style'

    @Override
    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        //Start
        if (!nodes[0] instanceof AnnotationNode || !nodes[1] instanceof ClassNode) {
            return
        }

        ClassNode classNode = nodes[1] as ClassNode

        checkMethodsToAddRenderCall(classNode)

        classNode.addProperty('shadowRoot', Modifier.PUBLIC , ClassHelper.OBJECT_TYPE, null, null, null)
        classNode.addProperty('cId', Modifier.PUBLIC , ClassHelper.Number_TYPE, null, null, null)

        HtmlMapVisitor htmlMapVisitor = new HtmlMapVisitor(classNode: classNode)
        classNode.visitContents(htmlMapVisitor)

        manageRenderMethod(classNode)
    }

    private static manageRenderMethod(ClassNode classNode) {
        MethodNode renderMethod = classNode.methods.find { it.name == RENDER_METHOD}
        if (!renderMethod) {
            throw new RuntimeException("You have to define a ${RENDER_METHOD} method")
        } else {
            BlockStatement actualCode = renderMethod.code as BlockStatement

            VariableScope variableScope = actualCode.getVariableScope()
            VariableScope blockScope = variableScope.copy()

            ClosureExpression closure = new ClosureExpression(Parameter.EMPTY_ARRAY, actualCode)
            VariableScope closureScope = variableScope.copy()
            closure.setVariableScope(closureScope)

            renderMethod.setCode(new BlockStatement([
                    new ExpressionStatement(
                            new BinaryExpression(
                                    new PropertyExpression(
                                            new PropertyExpression(
                                                    new VariableExpression('this', ClassHelper.OBJECT_TYPE),
                                                    'shadowRoot'
                                            ),
                                            'innerHTML'
                                    ),
                                    new Token(Types.ASSIGN, '=', 0, 0),
                                    htmlExpression(closure, classNode)
                            )
                    )
            ], blockScope))
        }
    }

    private static Expression htmlExpression(ClosureExpression closure, ClassNode classNode) {
        if (classHasStyle(classNode)) {
            def staticStyle = new VariableExpression(STYLE)
            new BinaryExpression(
                    new GStringExpression(null,
                            [new ConstantExpression('<style>'), new ConstantExpression('</style>')],
                            [new VariableExpression(staticStyle)]
                    ),
                    new Token(Types.PLUS, '+', 0, 0),
                    buildHtmlExpression(closure)
            )
        } else {
            buildHtmlExpression(closure)
        }
    }

    private static boolean classHasStyle(ClassNode classNode) {
        classNode.properties.find {
            it.name == STYLE && it.static
        }
    }

    private static Expression buildHtmlExpression(ClosureExpression closure) {
        new MethodCallExpression(
                new ClassExpression(new ClassNode(HtmlBuilder)),
                'build',
                new ArgumentListExpression([closure])
        )
    }

    private static void checkMethodsToAddRenderCall(ClassNode classNode) {
        PropertyNode renderAfter = classNode.properties.find { it.name == 'renderAfter' && it.static }
        if (renderAfter && renderAfter.initialExpression) {
            if (renderAfter.initialExpression instanceof ConstantExpression) {
                addRenderCallToMethod(renderAfter.initialExpression as ConstantExpression, classNode)
            }
            if (renderAfter.initialExpression instanceof ListExpression) {
                ListExpression list = renderAfter.initialExpression as ListExpression
                list.expressions.each { expression ->
                    if (expression instanceof ConstantExpression) {
                        addRenderCallToMethod(expression, classNode)
                    }
                }
            }
        }
    }

    private static void addRenderCallToMethod(ConstantExpression constantExpression, ClassNode classNode) {
        if (constantExpression.value instanceof String) {
            String method = constantExpression.value
            if (method != RENDER_METHOD && classNode.methods.any { it.name == method }) {
                MethodNode methodNode = classNode.methods.find { it.name == method }
                if (methodNode.code instanceof BlockStatement) {
                    BlockStatement block = methodNode.code as BlockStatement
                    block.addStatement(new ExpressionStatement(
                            new MethodCallExpression(
                                    new VariableExpression('this'),
                                    new ConstantExpression(RENDER_METHOD),
                                    new ArgumentListExpression([])
                            )
                    ))
                }
            }
        }
    }
}
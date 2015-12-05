package org.grooscript.grails.component

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.SourceUnit

/**
 *
 * @author Jorge Franco <jorge.franco@osoco.es>
 */
class HtmlMapVisitor extends ClassCodeVisitorSupport {

    ClassNode classNode

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        if (statement.expression instanceof MethodCallExpression) {
            def arguments = statement.expression.arguments
            if (arguments && arguments[0] instanceof MapExpression) {
                MapExpression mapExpression = (MapExpression)arguments[0]
                mapExpression.mapEntryExpressions.each { mapEntryExpression ->
                    Expression key = mapEntryExpression.keyExpression
                    Expression value = mapEntryExpression.valueExpression
                    if (key instanceof ConstantExpression &&
                            value instanceof ConstantExpression &&
                            key.value instanceof String &&
                            value.value instanceof String &&
                            key.value.toUpperCase().startsWith('ON') &&
                            isNameOfClassMethods(value.value)
                    ) {
                        String nameMethod = value.value
                        GStringExpression newExpression =
                                new GStringExpression(
                                        "GrooscriptGrails.recover(\$cId).${nameMethod}(this)",[
                                                new ConstantExpression('GrooscriptGrails.recover('),
                                                new ConstantExpression(").${nameMethod}(this)".toString())
                                        ], [
                                                new VariableExpression('cId')
                                        ])
                        mapEntryExpression.valueExpression = newExpression
                    }
                }
            }

        }
        super.visitExpressionStatement(statement)
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return null
    }

    private boolean isNameOfClassMethods(String name) {
        name in classNode.methods.collect { it.name }
    }
}

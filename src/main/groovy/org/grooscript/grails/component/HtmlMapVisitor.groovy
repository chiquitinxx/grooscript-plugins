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

import javax.annotation.ParametersAreNonnullByDefault

/**
 *
 * @author Jorge Franco <jorge.franco@osoco.es>
 */
@ParametersAreNonnullByDefault
class HtmlMapVisitor extends ClassCodeVisitorSupport {

    private ClassNode classNode

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        if (statement.expression instanceof MethodCallExpression) {
            MethodCallExpression mCallExpr = statement.expression as MethodCallExpression

            if (mCallExpr.arguments && mCallExpr.arguments[0] instanceof MapExpression) {
                MapExpression mapExpression = mCallExpr.arguments[0] as MapExpression

                mapExpression.mapEntryExpressions.each { mapEntryExpression ->
                    Expression key = mapEntryExpression.keyExpression
                    Expression value = mapEntryExpression.valueExpression

                    if (key instanceof ConstantExpression
                            && value instanceof ConstantExpression
                            && key.value instanceof String
                            && value.value instanceof String
                            && key.value.toUpperCase().startsWith('ON')
                            && isNameOfClassMethods(value.value as String)
                    ) {
                        String nameMethod = value.value
                        mapEntryExpression.valueExpression = new GStringExpression(
                                "GrooscriptGrails.recover(\$cId).${nameMethod}(this)",
                                [
                                        new ConstantExpression('GrooscriptGrails.recover('),
                                        new ConstantExpression(").${nameMethod}(this)".toString())
                                ],
                                [
                                        new VariableExpression('cId')
                                ]
                        )
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
        classNode.methods.any { it.name == name }
    }
}

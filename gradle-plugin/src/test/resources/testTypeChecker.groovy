import org.codehaus.groovy.ast.expr.MethodCallExpression

beforeMethodCall { call ->
    if (call instanceof MethodCallExpression) {
        //Not allowing use p tag in templates
        handled = (call.method.text != 'p')
    }
}

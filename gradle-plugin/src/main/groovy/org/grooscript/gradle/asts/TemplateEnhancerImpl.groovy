/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.gradle.asts

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
import org.grooscript.templates.Templates

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class TemplateEnhancerImpl implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        if (!nodes[0] instanceof AnnotationNode || !nodes[1] instanceof ClassNode) {
            return
        }

        ClassNode classNode = (ClassNode) nodes[1]

        def visitor = new ReplaceLayoutCallsVisitor()
        classNode.visitContents(visitor)

        visitor = new ReplaceIncludeCallsVisitor()
        classNode.visitContents(visitor)
    }
}

class ReplaceLayoutCallsVisitor extends ClassCodeVisitorSupport {

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        if (statement.expression instanceof MethodCallExpression &&
                statement.expression.methodAsString == 'layout') {
            def args = statement.expression.arguments
            ConstantExpression template = args.expressions[1]
            MapExpression mapExpression = args.expressions[0]
            changeLayoutMap(mapExpression)

            statement.expression = new MethodCallExpression(
                    new VariableExpression('this', ClassHelper.OBJECT_TYPE),
                    'yieldUnescaped',
                    new ArgumentListExpression([
                            new MethodCallExpression(
                                    new ClassExpression(new ClassNode(Templates)),
                                    'applyTemplate',
                                    new ArgumentListExpression([
                                            template,
                                            new BinaryExpression(
                                                    new VariableExpression('model', ClassHelper.MAP_TYPE),
                                                    new Token(Types.EQUALS, "+", -1, -1),
                                                    mapExpression
                                            )
                                    ])
                            )
                    ])
            )

        } else {
            super.visitExpressionStatement(statement)
        }
    }

    private changeLayoutMap(MapExpression mapExpression) {
        mapExpression.mapEntryExpressions.each { MapEntryExpression entry ->
            if (entry.valueExpression instanceof MethodCallExpression &&
                    entry.valueExpression.method.value == 'contents') {
                MethodCallExpression mce = entry.valueExpression
                ClosureExpression contentsClosure = mce.arguments[0]
                entry.valueExpression = new ClosureExpression(null, new BlockStatement(
                        [new ExpressionStatement(new MethodCallExpression(
                                new ClassExpression(new ClassNode(HtmlBuilder)),
                                'build',
                                new ArgumentListExpression([
                                        contentsClosure
                                ])
                        ))],
                        new VariableScope()
                ))
            }
        }
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return null
    }
}

class ReplaceIncludeCallsVisitor extends ClassCodeVisitorSupport {

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        if (statement.expression instanceof MethodCallExpression &&
                statement.expression.methodAsString == 'include') {
            def template
            try {
                def args = statement.expression.arguments

                args.expressions[0].mapEntryExpressions.each { MapEntryExpression mapEntryExpression ->
                    if (mapEntryExpression.keyExpression.text == 'template') {
                        template = mapEntryExpression.valueExpression.text
                    }
                }

                if (template) {
                    statement.expression = new MethodCallExpression(
                            new VariableExpression('this', ClassHelper.OBJECT_TYPE),
                            'yieldUnescaped',
                            new ArgumentListExpression([
                                    new MethodCallExpression(
                                            new ClassExpression(new ClassNode(Templates)),
                                            'applyTemplate',
                                            new ArgumentListExpression([
                                                    new ConstantExpression(template),
                                                    new VariableExpression('model', ClassHelper.MAP_TYPE)
                                            ])
                                    )
                            ])
                    )
                }
            } catch (e) {
                e.printStackTrace()
            }
        } else {
            super.visitExpressionStatement(statement)
        }
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return null
    }
}
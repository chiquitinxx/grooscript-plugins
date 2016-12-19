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
package org.grooscript.gradle.template

import groovy.text.SimpleTemplateEngine
import groovy.transform.TypeChecked
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.grooscript.util.GrooScriptException

class Generator {

    String TEMPLATES_TEMPLATE = '''package org.grooscript.gradle.template

@org.grooscript.gradle.asts.TemplateEnhancer
class Templates {

  static Map templates = $templates

  static String applyTemplate(String name, model = [:]) {
    def cl = templates[name]
    if (!cl) {
       '<p>Not found template: ' + name + '</p>'
    } else {
        cl.delegate = model
        cl(model)
    }
  }
}'''
    def classpath
    String customTypeChecker

    String generateClassCode(Map<String, String> templates) {
        compileTemplates(templates)
        def templatesFormat = templates.collect { entry ->
            [first: "'${entry.key}'",
             second: "{ model = [:] ->\n      HtmlBuilder.build {\n" +
                    "        ${entry.value}\n" +
                    "      }\n    }"]
        }.collect { strings ->
            '\n    ' + strings.first + ": " + strings.second
        }.join ','
        def engine = new SimpleTemplateEngine()
        engine.createTemplate(TEMPLATES_TEMPLATE).make([templates: '[' + templatesFormat + '\n  ]'])
    }

    private compileTemplates(Map<String, String> templates) {
        templates.each { entry ->
            try {
                CompilerConfiguration conf = new CompilerConfiguration()
                conf.addCompilationCustomizers(new SecureASTCustomizer(
                        //Added Autowired because is used when compiling groovy templates
                        //No idea why, maybe spring apps adds it!
                        importsWhitelist: ['org.springframework.beans.factory.annotation.Autowired'])
                )
                if (customTypeChecker) {
                    def acz = new ASTTransformationCustomizer(TypeChecked, extensions: customTypeChecker)
                    conf.addCompilationCustomizers(acz)
                }

                def scriptClassName = 'script' + System.currentTimeMillis()
                def parent = new GroovyClassLoader()
                addClassPathToGroovyClassLoader(parent)
                GroovyClassLoader classLoader = new GroovyClassLoader(parent, conf)
                addClassPathToGroovyClassLoader(classLoader)
                compileCode(conf, scriptClassName, classLoader, entry.value)
            } catch (Exception e) {
                throw new GrooScriptException("Templates generator, failed compile template ${entry.key}: ${e.message}")
            }
        }
    }

    private addClassPathToGroovyClassLoader(classLoader) {
        if (classpath) {
            if (!(classpath instanceof String || classpath instanceof Collection)) {
                throw new GrooScriptException('The classpath must be a String or a List')
            }

            if (classpath instanceof Collection) {
                classpath.each {
                    classLoader.addClasspath(it)
                }
            } else {
                classLoader.addClasspath(classpath)
            }
        }
    }

    private CompilationUnit compileCode(CompilerConfiguration conf, String scriptClassName,
                                        GroovyClassLoader classLoader, String code) {
        def compilationUnitFinal = new CompilationUnit(conf, null, classLoader)
        compilationUnitFinal.addSource(scriptClassName, code)
        compilationUnitFinal.compile(CompilePhase.INSTRUCTION_SELECTION.phaseNumber)
        compilationUnitFinal
    }
}

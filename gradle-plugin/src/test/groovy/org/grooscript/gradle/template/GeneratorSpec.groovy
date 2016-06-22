package org.grooscript.gradle.template

import org.grooscript.GrooScript
import org.grooscript.test.JsTestResult
import org.grooscript.util.GrooScriptException
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 08/10/14
 */
class GeneratorSpec extends Specification {

    def 'templates are compiled before generate code'() {
        given:
        def templates = ['one.gtpl': "p 'Hello!"]

        when:
        generator.generateClassCode(templates)

        then:
        thrown(GrooScriptException)
    }

    def 'not supporting imports in templates'() {
        given:
        def templates = ['one.gtpl': "import org.grooscript.GrooScript;p 'Hello!'"]

        when:
        generator.generateClassCode(templates)

        then:
        thrown(GrooScriptException)
    }

    def 'supporting spring autowire import'() {
        given:
        def templates = ['one.gtpl': "import org.springframework.beans.factory.annotation.Autowired;p 'Hello!'"]

        when:
        generator.generateClassCode(templates)

        then:
        notThrown(GrooScriptException)
    }

    def 'custom type checking'() {
        given:
        def templates = ['one.gtpl': "p 'Hello!'"]

        when:
        generator.classpath = 'src/test/resources'
        generator.customTypeChecker = 'testTypeChecker.groovy'
        generator.generateClassCode(templates)

        then:
        thrown(GrooScriptException)
    }

    def 'generate first template'() {
        given:
        def templates = ['one.gtpl': "p 'Hello!'"]

        expect:
        generator.generateClassCode(templates) == '''package org.grooscript.gradle.template

@org.grooscript.gradle.asts.TemplateEnhancer
class Templates {

  static Map templates = [
    'one.gtpl': { model = [:] ->
      HtmlBuilder.build {
        p 'Hello!'
      }
    }
  ]

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
    }

    def 'generate two templates'() {
        given:
        def templates = ['one.gtpl': "p 'Hello!'", 'two.gtpl': "p 'Bye!'"]

        expect:
        generator.generateClassCode(templates) == '''package org.grooscript.gradle.template

@org.grooscript.gradle.asts.TemplateEnhancer
class Templates {

  static Map templates = [
    'one.gtpl': { model = [:] ->
      HtmlBuilder.build {
        p 'Hello!'
      }
    },
    'two.gtpl': { model = [:] ->
      HtmlBuilder.build {
        p 'Bye!'
      }
    }
  ]

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
    }

    def 'convert a template with a model variable'() {
        given:
        def templates = ['hello.gtpl': "p 'Hello ' + model.name+ '!'"]
        def code = generator.generateClassCode(templates)
        code += "\nprintln Templates.applyTemplate('hello.gtpl', [name: 'Jorge'])\n"

        when:
        JsTestResult result = GrooScript.evaluateGroovyCode(code, 'grooscript-tools')

        then:
        !result.exception
        result.console == '<p>Hello Jorge!</p>'
    }

    def 'convert a template with a variable'() {
        given:
        def templates = ['hello.gtpl': "p 'Hello ' + name+ '!'"]
        def code = generator.generateClassCode(templates)
        code += "\nprintln Templates.applyTemplate('hello.gtpl', [name: 'Jorge'])\n"

        when:
        JsTestResult result = GrooScript.evaluateGroovyCode(code, 'grooscript-tools')

        then:
        !result.exception
        result.console == '<p>Hello Jorge!</p>'
    }

    def 'convert a template with an include'() {
        given:
        def templates = ['hello.gtpl': "p 'Hello ' + name+ '!'",
                         'initial.gtpl': "[1, 2].each { include template: 'hello.gtpl'}"]
        def code = generator.generateClassCode(templates)
        code += "\nprintln Templates.applyTemplate('initial.gtpl', [name: 'Jorge'])\n"

        when:
        JsTestResult result = GrooScript.evaluateGroovyCode(code, 'grooscript-tools')

        then:
        !result.exception
        result.console == '<p>Hello Jorge!</p><p>Hello Jorge!</p>'
    }

    def 'convert a template with a closure'() {
        given:
        def templates = ['hello.gtpl': "3.times { p 'Hi!' }; p 'Hello ' + name+ '!'"]
        def code = generator.generateClassCode(templates)
        code += "\nprintln Templates.applyTemplate('hello.gtpl', [name: 'Jorge'])\n"

        when:
        JsTestResult result = GrooScript.evaluateGroovyCode(code, 'grooscript-tools')

        then:
        !result.exception
        result.console == '<p>Hi!</p><p>Hi!</p><p>Hi!</p><p>Hello Jorge!</p>'
    }

    def 'convert yield text'() {
        given:
        def templates = ['hello.gtpl': "yieldUnescaped '<!DOCTYPE html>'; yield 'GOAL<>'"]
        def code = generator.generateClassCode(templates)
        code += "\nprintln Templates.applyTemplate('hello.gtpl')\n"

        when:
        JsTestResult result = GrooScript.evaluateGroovyCode(code, 'grooscript-tools')

        then:
        !result.exception
        result.console == '<!DOCTYPE html>GOAL&lt;&gt;'
    }

    def 'convert a template with a layout'() {
        given:
        def templates = ['hello.tpl': "layout 'layout.tpl', salute: 'Hello ' + name +'!', bye: contents { p 'Bye '+ name + '.' }",
                         'layout.tpl': "p(salute); bye()"]
        def code = generator.generateClassCode(templates)
        code += "\nprintln Templates.applyTemplate('hello.tpl', [name: 'Jorge'])\n"

        when:
        JsTestResult result = GrooScript.evaluateGroovyCode(code, 'grooscript-tools')

        then:
        !result.exception
        result.console == '<p>Hello Jorge!</p><p>Bye Jorge.</p>'
    }

    def 'try to convert a template that doesn\'t exists'() {
        given:
        def templates = ['hello.gtpl': "p 'Hello!'"]
        def code = generator.generateClassCode(templates)
        code += "\nprintln Templates.applyTemplate('notExists.tpl')\n"

        when:
        JsTestResult result = GrooScript.evaluateGroovyCode(code, 'grooscript-tools')

        then:
        !result.exception
        result.console == '<p>Not found template: notExists.tpl</p>'
    }

    Generator generator = new Generator()
}

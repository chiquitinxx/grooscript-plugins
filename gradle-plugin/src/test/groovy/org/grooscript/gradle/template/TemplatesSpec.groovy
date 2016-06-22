package org.grooscript.gradle.template

import groovy.text.Template
import groovy.text.markup.MarkupTemplateEngine
import groovy.text.markup.TemplateConfiguration
import spock.lang.Specification

import static org.grooscript.util.Util.SEP

/**
 * Created by jorge on 04/10/14.
 */
class TemplatesSpec extends Specification {

    void 'converting a template'() {
        expect:
        convertTemplate("src${SEP}test${SEP}resources${SEP}one.gtpl") == '<p>Hello!</p>'
        convertTemplate("src${SEP}test${SEP}resources${SEP}three.gtpl", [list: [1, 2]]) ==
                '<ul><p>Hello!</p><p>Hello!</p></ul>'
    }

    private convertTemplate(pathToFile, model = null) {
        TemplateConfiguration config = new TemplateConfiguration()
        MarkupTemplateEngine engine = new MarkupTemplateEngine(config)
        Template template = engine.createTemplate(new File(pathToFile).text)
        template.make(model).toString()
    }
}

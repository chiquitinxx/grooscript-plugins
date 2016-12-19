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

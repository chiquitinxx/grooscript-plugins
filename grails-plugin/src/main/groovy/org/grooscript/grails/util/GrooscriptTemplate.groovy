package org.grooscript.grails.util

import groovy.text.SimpleTemplateEngine
import org.springframework.stereotype.Component

@Component
class GrooscriptTemplate {

    private engine = new SimpleTemplateEngine()

    String apply(String template, Map binding = [:]) {
        engine.createTemplate(template).make(binding).toString()
    }
}

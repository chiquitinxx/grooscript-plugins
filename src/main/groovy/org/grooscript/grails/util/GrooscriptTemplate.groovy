package org.grooscript.grails.util

import groovy.text.SimpleTemplateEngine

/**
 * @author jorgefrancoleza
 */
class GrooscriptTemplate {

    private engine = new SimpleTemplateEngine()

    String apply(String template, Map binding = [:]) {
        engine.createTemplate(template).make(binding).toString()
    }
}

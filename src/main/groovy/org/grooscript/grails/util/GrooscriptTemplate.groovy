package org.grooscript.grails.util

import groovy.text.SimpleTemplateEngine

/**
 * User: jorgefrancoleza
 * Date: 11/06/14
 */
class GrooscriptTemplate {

    private engine = new SimpleTemplateEngine()

    String apply(String template, Map binding = [:]) {
        engine.createTemplate(template).make(binding).toString()
    }
}

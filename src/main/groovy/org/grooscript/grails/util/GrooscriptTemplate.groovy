package org.grooscript.grails.util

import groovy.text.SimpleTemplateEngine

/**
 * @author Jorge Franco <jorge.franco@osoco.es>
 */
class GrooscriptTemplate {

    private engine = new SimpleTemplateEngine()

    String apply(String template, Map binding = [:]) {
        engine.createTemplate(template).make(binding).toString()
    }
}

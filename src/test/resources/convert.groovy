@Grab('org.grooscript:grooscript:1.2.3')

import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import org.grooscript.grails.component.Component

def conversionOptions = [:]
conversionOptions[ConversionOptions.CLASSPATH.text] = '../../../src/main/groovy'
conversionOptions[ConversionOptions.CUSTOMIZATION.text] = {
    ast(Component)
}
GrooScript.convert('Counter.groovy', '.', conversionOptions)
package org.grooscript.gradle

class ConversionExtension {
    def source = ['src/main/groovy']
    String destination = 'src/main/webapp/js/app'
    List<String> classpath = ['src/main/groovy']
    Closure customization
    String initialText
    String finalText
    boolean recursive = false
    List<String> mainContextScope
    String addGsLib
    boolean includeDependencies = false
    boolean consoleInfo = false
    boolean nashornConsole = false
}

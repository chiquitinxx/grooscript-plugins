package org.grooscript.gradle

class RequireJsExtension {
    String sourceFile
    List<String> classpath = ['src/main/groovy']
    String destinationFolder
    Closure customization
    String initialText
    String finalText
    List<String> mainContextScope
    boolean nashornConsole = false
}

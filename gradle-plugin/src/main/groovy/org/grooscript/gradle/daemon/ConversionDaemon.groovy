package org.grooscript.gradle.daemon

import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import org.grooscript.convert.util.DependenciesSolver
import org.grooscript.util.GsConsole
import org.grooscript.util.Util

/**
 * User: jorgefrancoleza
 * Date: 21/02/13
 */
class ConversionDaemon {

    static numberConversions = 0

    static Closure conversionClosure = { List<String> source, String destination,
                                         Map conversionOptions, List<String> filesChanged ->
        List<String> filesToConvert = filesChanged.findAll {
            hasGoodExtension(it)
        }
        if (filesToConvert) {
            if (destination.endsWith(Util.JS_EXTENSION) ||
                    conversionOptions[ConversionOptions.INCLUDE_DEPENDENCIES.text] == true) {
                GrooScript.convert(source, destination, conversionOptions)
            } else {
                GrooScript.convert(filesToConvert, destination, conversionOptions)
            }
            GsConsole.info "[${numberConversions++}] Conversion daemon has converted files."
        }
    }

    static FilesDaemon start(List<String> source, String destination, Map conversionOptions) {
        List<String> daemonSource = source
        if (conversionOptions && conversionOptions[ConversionOptions.INCLUDE_DEPENDENCIES.text] == true) {
            daemonSource = includeDependencies(daemonSource, conversionOptions)
        }
        def filesDaemon = new FilesDaemon(daemonSource,
                newAction(source, destination, conversionOptions),
                [actionOnStartup: true, recursive: conversionOptions.recursive ?: false])
        filesDaemon.start()
        filesDaemon
    }

    private static Closure newAction(List<String> source, String destination, Map conversionOptions) {
        conversionClosure.curry(source, destination, conversionOptions)
    }

    private static List<String> includeDependencies(List<String> sources, Map conversionOptions) {
        List<String> all = sources.clone()
        DependenciesSolver dependenciesSolver = GrooScript.newDependenciesSolver(conversionOptions)
        sources.each { source ->
            def file = new File(source)
            if (validFile(file)) {
                all.addAll dependenciesSolver.processCode(file.text).collect { it.path }
            } else if (file.exists() && file.isDirectory()) {
                file.eachFile {
                    if (validFile(it)) {
                        all.addAll dependenciesSolver.processCode(it.text).collect { it.path }
                    }
                }
            }
        }
        all.unique()
    }

    private static boolean validFile(File file) {
        file && file.exists() && file.isFile() && hasGoodExtension(file.path)
    }

    private static boolean hasGoodExtension(String path) {
        path.endsWith(Util.GROOVY_EXTENSION) || path.endsWith(Util.JAVA_EXTENSION)
    }
}

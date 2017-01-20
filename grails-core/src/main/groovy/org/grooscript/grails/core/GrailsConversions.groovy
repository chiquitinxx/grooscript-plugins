package org.grooscript.grails.core

import org.grooscript.convert.ConversionOptions
import org.grooscript.grails.core.converter.Converter
import org.grooscript.grails.core.converter.CoreConverter
import org.grooscript.grails.core.util.FileSupport
import org.grooscript.grails.core.util.GradleFile

import java.util.regex.Matcher

class GrailsConversions implements Conversion {

    private static final String SEP = System.getProperty('file.separator')

    static final List DEFAULT_CONVERSION_SCOPE_VARS = [
            '$', 'gsEvents', 'window', 'document', 'HtmlBuilder', 'GsHlp',
            'GQueryImpl', 'Observable', 'ClientEventHandler', 'GrooscriptGrails']
    static final String GROOVY_SRC_DIR = "src${SEP}main${SEP}groovy"

    private Converter converter = new CoreConverter()
    private FileSupport fileSupport = new GradleFile()

    void setBaseDir(String path) {
        fileSupport.setBaseDir(path)
    }

    @Override
    String convertToJavascript(String groovyCode, Map conversionOptions) {
        Map options = addDefaultOptions(conversionOptions ?: [:])
        converter.convert(groovyCode, options)
    }

    @Override
    String convertComponent(String componentClassFullName, String nameComponent) {
        Map conversionOptions = [:]
        conversionOptions[ConversionOptions.CLASSPATH.text] = [GROOVY_SRC_DIR]
        conversionOptions[ConversionOptions.INCLUDE_DEPENDENCIES.text] = true
        String shortClassName = getShortName(componentClassFullName)
        String componentGroovyCode = getClassSource(componentClassFullName)

        converter.convertComponent(componentGroovyCode, addGrailsDefaultJsMainContextScopeVars(conversionOptions)) +
                getComponentRegisterCode(shortClassName, nameComponent)
    }

    @Override
    List saveConversionForPackaging(File path, String fileName, String jsCode) {
        boolean result = false
        String fullPathFileName = path.absolutePath + SEP + fileName
        String errorMessage
        try {
            new File(fullPathFileName).text = jsCode
            result = true
        } catch (Throwable t) {
            errorMessage = "Error saving file (${fullPathFileName}) for packaging. Error: ${t.getMessage()}"
        }
        [result, errorMessage]
    }

    static String getFileNameFromClassCanonicalName(String fullClassCanonicalName) {
        "${fullClassCanonicalName.replaceAll("\\.", Matcher.quoteReplacement(SEP))}.groovy"
    }

    private Map addDefaultOptions(Map options) {
        if (!options[ConversionOptions.CLASSPATH.text])
            options[ConversionOptions.CLASSPATH.text] = []
        else {
            if (options[ConversionOptions.CLASSPATH.text] instanceof String)
                options[ConversionOptions.CLASSPATH.text] =
                        [options[ConversionOptions.CLASSPATH.text]]
        }

        if (!options[ConversionOptions.CLASSPATH.text].contains(GROOVY_SRC_DIR))
            options[ConversionOptions.CLASSPATH.text] << GROOVY_SRC_DIR

        if (options[ConversionOptions.INCLUDE_DEPENDENCIES.text] == null) {
            options[ConversionOptions.INCLUDE_DEPENDENCIES.text] = true
        }

        addGrailsDefaultJsMainContextScopeVars(options)
    }

    private Map addGrailsDefaultJsMainContextScopeVars(Map options) {
        if (!options[ConversionOptions.MAIN_CONTEXT_SCOPE.text])
            options[ConversionOptions.MAIN_CONTEXT_SCOPE.text] = []

        options[ConversionOptions.MAIN_CONTEXT_SCOPE.text] =
                options[ConversionOptions.MAIN_CONTEXT_SCOPE.text] + DEFAULT_CONVERSION_SCOPE_VARS
        options
    }

    private String getComponentRegisterCode(String className, String nameComponent) {
        ";GrooscriptGrails.createComponent(${className}, '${nameComponent}');"
    }

    private String getClassSource(String fullClassName) {
        getFileContentsIfExists(GROOVY_SRC_DIR, fullClassName)
    }

    private String getFileContentsIfExists(String baseDir, String fileClassName) {
        def filePath = "${baseDir}${SEP}${getFileNameFromClassCanonicalName(fileClassName)}"
        fileSupport.getFileContent(filePath)
    }

    private String getShortName(String fullClassName) {
        String result = fullClassName
        int i = result.lastIndexOf(".")
        if (i > -1) {
            result = result.substring(i + 1, result.length())
        }
        result
    }
}

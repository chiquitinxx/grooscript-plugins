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
package org.grooscript.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import org.grooscript.convert.util.ConvertedFile

class RequireJsAbstractTask extends DefaultTask {

    String sourceFile
    List<String> classpath
    String destinationFolder
    Closure customization
    String initialText
    String finalText
    List<String> mainContextScope
    boolean nashornConsole

    void checkProperties() {
        sourceFile = sourceFile ?: project.extensions.requireJs?.sourceFile
        if (sourceFile)
            sourceFile = project.file(sourceFile).path
        destinationFolder = destinationFolder ?: project.extensions.requireJs?.destinationFolder
        if (destinationFolder)
            destinationFolder = project.file(destinationFolder).path
        customization = customization ?: project.extensions.requireJs?.customization
        initialText = initialText ?: project.extensions.requireJs?.initialText
        finalText = finalText ?: project.extensions.requireJs?.finalText
        mainContextScope = mainContextScope ?: project.extensions.requireJs?.mainContextScope
        classpath = classpath ?: project.extensions.requireJs?.classpath
        if (classpath)
            classpath = classpath.collect { project.file(it).path }
        nashornConsole = nashornConsole ?: project.extensions.requireJs?.nashornConsole
    }

    void errorParameters() {
        throw new GradleException(
            'For require js, have to define task properties: sourceFile, classpath, destinationFolder\n'+
                    "  sourceFile = 'src/main/groovy/Start.groovy'\n"+
                    "  classpath = ['src/main/groovy']\n"+
                    "  destinationFolder = 'src/main/webapp/js'"
        )
    }

    List<ConvertedFile> convertRequireJsFile() {
        Map conversionOptions = [:]
        conversionOptions[ConversionOptions.CLASSPATH.text] = classpath
        conversionOptions[ConversionOptions.INITIAL_TEXT.text] = initialText
        conversionOptions[ConversionOptions.FINAL_TEXT.text] = finalText
        conversionOptions[ConversionOptions.MAIN_CONTEXT_SCOPE.text] = mainContextScope
        conversionOptions[ConversionOptions.CUSTOMIZATION.text] = customization
        conversionOptions[ConversionOptions.NASHORN_CONSOLE.text] = nashornConsole
        GrooScript.convertRequireJs(sourceFile, destinationFolder, conversionOptions)
    }
}

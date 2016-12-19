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
import org.grooscript.convert.ConversionOptions

class GrooscriptTask extends DefaultTask {

    List<String> source
    String destination
    List<String> classpath
    Closure customization
    String initialText
    String finalText
    boolean recursive = false
    List<String> mainContextScope
    String addGsLib
    boolean requireJsModule = false
    boolean consoleInfo
    boolean includeDependencies
    boolean nashornConsole

    void checkProperties() {
        source = source ?: project.extensions.grooscript?.source
        destination = destination ?: project.extensions.grooscript?.destination
        classpath = classpath ?: project.extensions.grooscript?.classpath
        classpath = classpath.collect { project.file(it).path }
        customization = customization ?: project.extensions.grooscript?.customization
        initialText = initialText ?: project.extensions.grooscript?.initialText
        finalText = finalText ?: project.extensions.grooscript?.finalText
        recursive = recursive ?: project.extensions.grooscript?.recursive
        mainContextScope = mainContextScope ?: project.extensions.grooscript?.mainContextScope
        addGsLib = addGsLib ?: project.extensions.grooscript?.addGsLib
        includeDependencies = includeDependencies ?: project.extensions.grooscript?.includeDependencies
        consoleInfo = consoleInfo ?: project.extensions.grooscript?.consoleInfo
        nashornConsole = nashornConsole ?: project.extensions.grooscript?.nashornConsole
    }

    Map getConversionProperties() {
        ConversionOptions.values().collect { it.text }.inject([:]) { properties, property ->
            properties.put(property, this."$property")
            properties
        }
    }
}

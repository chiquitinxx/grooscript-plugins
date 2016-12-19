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
import org.grooscript.gradle.template.Generator
import org.grooscript.util.GsConsole

class TemplatesAbstractTask extends DefaultTask {

    String templatesPath
    List<String> templates
    String destinationFile
    List<String> classpath
    String customTypeChecker

    void checkProperties() {
        templatesPath = templatesPath ?: project.extensions.templates?.templatesPath
        if (templatesPath)
            templatesPath = project.file(templatesPath).path
        destinationFile = destinationFile ?: project.extensions.templates?.destinationFile
        if (destinationFile)
            destinationFile = project.file(destinationFile).path
        templates = templates ?: project.extensions.templates?.templates
        classpath = classpath ?: project.extensions.templates?.classpath
        if (classpath)
            classpath = classpath.collect { project.file(it).path }
    }

    void errorParameters() {
        throw new GradleException(
            'For templates, have to define task properties: templatesPath, templates, destinationPath\n'+
                    "  templatesPath = 'src/main/resources/templates'\n"+
                    "  templates = ['one.gtpl', 'folder/two.tpl']\n"+
                    "  destinationFile = 'src/main/webapp/js'"
        )
    }

    Map getConversionOptions() {
        def options = [:]
        options.put(ConversionOptions.MAIN_CONTEXT_SCOPE.text, ['HtmlBuilder'])
        options.put(ConversionOptions.CLASSPATH.text, classpath)
        options
    }

    protected generateTemplate() {
        String classCode = new Generator(classpath: classpath, customTypeChecker: customTypeChecker).
                generateClassCode(getTemplatesMap())
        if (!destinationFile.toUpperCase().endsWith('.JS')) {
            throw new GradleException('destinationFile has to be a js file: ' + destinationFile)
        }
        new File(destinationFile).text = doConversion(classCode)
        GsConsole.message("Generated template ${destinationFile}.")
    }

    private Map getTemplatesMap() {
        File path = new File(templatesPath)
        if (!path.isDirectory()) {
            throw new GradleException('templatesPath has to be a folder: ' + templatesPath)
        }
        templates.inject([:]) { templates, filePath ->
            templates << ["${filePath}": new File(path.absolutePath + '/' + filePath).text]
        }
    }

    private String doConversion(String classCode) {
        GrooScript.convert(classCode, conversionOptions)
    }
}

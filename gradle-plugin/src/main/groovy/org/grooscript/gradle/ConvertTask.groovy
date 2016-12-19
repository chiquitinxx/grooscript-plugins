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

import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.grooscript.GrooScript

class ConvertTask extends GrooscriptTask {

    @TaskAction
    void convert() {
        checkProperties()
        if (!source || !destination) {
            throw new GradleException("Need define source and destination.")
        } else {
            doConversion()
        }
    }

    private void doConversion() {
        GrooScript.convert(source.collect { project.file(it) }, project.file(destination), conversionProperties)
    }
}

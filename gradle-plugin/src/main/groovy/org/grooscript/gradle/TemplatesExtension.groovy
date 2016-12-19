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

class TemplatesExtension {
    String templatesPath = 'src/main/webapp/templates'
    List<String> templates
    String destinationFile = 'src/main/webapp/js/lib/Templates.js'
    List<String> classpath = ['src/main/groovy']
    String customTypeChecker
}

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
package org.grooscript.gradle.grails

import org.grooscript.gradle.util.TagsFinder

class GrailsServerPages {

    TagsFinder tagsFinder = new TagsFinder()

    List<File> find(String starterPath) {
        List<File> result = []
        new File(starterPath).eachFileRecurse { file ->
            if (file.isFile() && file.name.toUpperCase().endsWith('.GSP')) {
                result << file
            }
        }
        result
    }

    List<Map<String, String>> getComponents(List<File> gsps) {
        extractTagsFromFiles(gsps, 'grooscript:component')
    }

    private List<Map<String, String>> extractTagsFromFiles(List<File> gsps, String tag) {
        gsps.collect({ File file -> tagsFinder.getAttributes(tag, file.text) })
                .flatten()
                .unique()
    }
}

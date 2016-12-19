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
package org.grooscript.gradle.util

import org.grooscript.GrooScript

class InitToolsImpl implements InitTools {
    boolean existsFile(String fileRelative) {
        def file = new File(fileRelative)
        file && file.exists() && file.isFile()
    }

    boolean createDirs(String dirRelative) {
        new File(dirRelative).mkdirs()
        true
    }

    boolean saveFile(String fileRelative, String content) {
        new File(fileRelative).text = content
        true
    }

    boolean extractGrooscriptJarFile(String fileName, String fileDestinationRelative) {
        new File(fileDestinationRelative).text =
            GrooScript.classLoader.getResourceAsStream('META-INF/resources/' + fileName).text
        true
    }

    boolean saveRemoteFile(String fileRelative, String url) {
        new File(fileRelative).text = url.toURL().text
        true
    }
}

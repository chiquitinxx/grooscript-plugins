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
package org.grooscript.gradle.require

import groovyx.gpars.actor.DefaultActor
import org.grooscript.convert.util.ConvertedFile
import org.grooscript.util.GsConsole

class RequireJsActor extends DefaultActor {

    private static final WAIT_TIME = 100
    private List<ConvertedFile> listFiles = []
    private Map<String, Long> dateSourceFiles = [:]
    Closure convertAction

    public static RequireJsActor getInstance() {
        new RequireJsActor()
    }

    void act() {
        loop {
            react { source ->

                if (anyFileChanged()) {
                    try {
                        listFiles = convertAction()
                        GsConsole.message("Require.js modules generated from $source")
                    } catch (Throwable e) {
                        GsConsole.error("Error generating require.js modules from $source. Exception: ${e.message}")
                    }
                    updateFilesDateTimes()
                }
                sleep(WAIT_TIME)
                this << source
            }
        }
    }

    public void onException(Throwable e) {
        GsConsole.exception('Exception in RequireJsActor: ' + e.message)
    }

    public void onInterrupt(InterruptedException e) {
        GsConsole.error('InterruptedException in RequireJsActor: ' + e.message)
    }

    private updateFilesDateTimes() {
        listFiles.each {
            dateSourceFiles[it.sourceFilePath] = new File(it.sourceFilePath).lastModified()
        }
    }

    private boolean anyFileChanged() {
        !listFiles || listFiles.any {
            !dateSourceFiles[it.sourceFilePath] ||
                    (dateSourceFiles[it.sourceFilePath] != new File(it.sourceFilePath).lastModified())
        }
    }
}

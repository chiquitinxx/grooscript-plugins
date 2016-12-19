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

import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.require.RequireJsActor

class RequireJsThreadTask extends RequireJsAbstractTask {

    private static final WAIT_TIME = 200
    boolean blockExecution = false

    @TaskAction
    void startThread() {
        checkProperties()
        if (sourceFile && destinationFolder && classpath) {
            def actor = configureAndStartDaemon()
            if (blockExecution) {
                def thread = Thread.start {
                    while (actor?.isActive()) {
                        sleep(WAIT_TIME)
                    }
                }
                thread.join()
            }
        } else {
            errorParameters()
        }
    }

    private RequireJsActor configureAndStartDaemon() {
        def action = this.&convertRequireJsFile
        action.setDelegate(this)
        action.resolveStrategy = Closure.DELEGATE_ONLY
        def actor = RequireJsActor.getInstance()
        actor.convertAction = action
        actor.start()
        actor << sourceFile
        actor
    }
}

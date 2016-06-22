package org.grooscript.gradle

import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.require.RequireJsActor

/**
 * Created by jorgefrancoleza on 12/5/15.
 */
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

package org.grooscript.gradle.require

import groovyx.gpars.actor.DefaultActor
import org.grooscript.convert.util.ConvertedFile
import org.grooscript.util.GsConsole

/**
 * Created by jorgefrancoleza on 19/5/15.
 */
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

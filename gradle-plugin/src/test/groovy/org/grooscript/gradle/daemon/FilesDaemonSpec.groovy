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
package org.grooscript.gradle.daemon

import org.grooscript.util.GsConsole
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class FilesDaemonSpec extends Specification {

    void 'initialize the daemon'() {
        given:
        def daemon = new FilesDaemon(files, ACTION)

        expect:
        daemon.action.is ACTION
        daemon.files == files
        daemon.options == [
            actionOnStartup: false,
            time: 200,
            recursive: false,
            checkDependencies: false
        ]
        !daemon.actor
    }

    void 'initialize the daemon with other options'() {
        given:
        def daemon = new FilesDaemon(files, ACTION, NEW_OPTIONS)

        expect:
        daemon.options == NEW_OPTIONS + [checkDependencies:false]
    }

    void 'if starts with actions on start up, run them'() {
        given:
        def actionExecutions = 0
        def daemon = new FilesDaemon(files, { List<String> changingFiles ->
            assert files == changingFiles
            actionExecutions++
        }, NEW_OPTIONS)

        when:
        daemon.start()
        daemon.stop()

        then:
        actionExecutions == 1
    }

    void 'show error if exception in action at start, and continue execution'() {
        given:
        def conditions = new PollingConditions(initialDelay: 0.3)
        GroovySpy(GsConsole, global:true)
        def daemon = new FilesDaemon(files, { list ->
            assert list == [tempFile.path]
            throw new Exception('error')
        }, [actionOnStartup: true])

        when:
        daemon.start()

        then:
        1 * GsConsole.error("Error executing action at start in files ([${tempFile.path}]): error")
        conditions.eventually {
            assert daemon.actor.isActive()
        }

        cleanup:
        daemon.stop()
    }

    void 'change detected and continue execution'() {
        given:
        def timesChanged = 0
        def daemon = new FilesDaemon(files, { List<String> files ->
            assert files == [tempFile.path]
            timesChanged++
        }, [actionOnStartup: true])

        when:
        def conditions = new PollingConditions()
        daemon.start()

        then:
        conditions.eventually {
            assert timesChanged == 1
        }

        when:
        sleep(1000)
        modifyFile()
        conditions = new PollingConditions()

        then:
        conditions.eventually {
            assert timesChanged == 2
            assert daemon.actor.isActive()
        }

        cleanup:
        daemon.stop()
    }

    private File tempFile
    private List files

    private static final ACTION = { files -> files }
    private static final NEW_OPTIONS = [
        actionOnStartup: true,
        time: 300,
        recursive: true
    ]
    private static final FILE1_NAME = 'File1'

    private modifyFile() {
        tempFile.text = 'pepe'
    }

    def setup() {
        tempFile = File.createTempFile(FILE1_NAME, 'groovy')
        tempFile.text = 'class File1 {}'
        files = [tempFile.path]
    }

    def cleanup() {
        tempFile.delete()
    }
}

package org.grooscript.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.grooscript.gradle.util.InitTools
import spock.lang.Specification

import static org.grooscript.gradle.InitStaticWebTask.*
/**
 * Created by jorge on 15/02/14.
 */
class InitStaticWebTaskSpec extends Specification {

    def 'create the task'() {
        expect:
        task instanceof InitStaticWebTask
    }

    def 'can\'t init if index.html file already exists'() {
        when:
        task.initStaticWeb()
        println project.projectDir

        then:
        1 * initTools.existsFile(projectDir(HTML_FILE)) >> true
        0 * _
        thrown(GradleException)
    }

    def 'init static web'() {
        when:
        task.initStaticWeb()

        then:
        1 * initTools.existsFile(projectDir(HTML_FILE)) >> false
        1 * initTools.createDirs(projectDir(JS_LIB_DIR)) >> true
        1 * initTools.createDirs(projectDir(JS_APP_DIR)) >> true
        1 * initTools.createDirs(projectDir(GROOVY_DIR)) >> true
        1 * initTools.saveFile(projectDir(HTML_FILE), task.HTML_TEXT) >> true
        1 * initTools.saveFile(projectDir(PRESENTER_FILE), task.PRESENTER_TEXT) >> true
        1 * initTools.extractGrooscriptJarFile('grooscript.min.js',
                projectDir(JS_LIB_DIR) + SEP + GROOSCRIPT_MIN_JS_NAME) >> true
        1 * initTools.extractGrooscriptJarFile('grooscript-tools.js',
                projectDir(JS_LIB_DIR) + SEP + GROOSCRIPT_TOOLS_JS_NAME) >> true
        1 * initTools.saveRemoteFile(projectDir(JQUERY_JS_FILE), task.JQUERY_JS_REMOTE) >> true
        0 * _
        noExceptionThrown()
    }

    Project project
    InitStaticWebTask task
    InitTools initTools

    def setup() {
        initTools = Mock(InitTools)
        project = ProjectBuilder.builder().build()
        task = project.task('initStaticWeb', type: InitStaticWebTask)
        task.initTools = initTools
        task.project = project
    }

    private projectDir(path) {
        project.projectDir.absolutePath + SEP + path
    }
}

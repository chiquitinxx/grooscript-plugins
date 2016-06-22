package org.grooscript.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.grooscript.gradle.util.InitTools

/**
 * Created by jorge on 15/02/14.
 */
class InitStaticWebTask extends DefaultTask {

    public static final String SEP = System.getProperty('file.separator')
    public static final String GROOVY_DIR = "src${SEP}main${SEP}groovy"
    public static final String PRESENTER_FILE = GROOVY_DIR + SEP + "Presenter.groovy"
    public static final String WEBAPP_DIR = "src${SEP}main${SEP}webapp"
    public static final String JS_DIR = WEBAPP_DIR + SEP + "js"
    public static final String JS_LIB_DIR = JS_DIR + SEP + "lib"
    public static final String JS_APP_DIR = JS_DIR + SEP + "app"
    public static final String HTML_FILE = WEBAPP_DIR + SEP +"index.html"
    public static final String GROOSCRIPT_MIN_JS_NAME = 'grooscript.min.js'
    public static final String GROOSCRIPT_TOOLS_JS_NAME = 'grooscript-tools.js'
    public static final String JQUERY_JS_FILE = JS_LIB_DIR + SEP + "jquery.min.js"
    public static final String JQUERY_JS_REMOTE = 'http://code.jquery.com/jquery-1.11.0.min.js'

    static final PRESENTER_TEXT = '''
class Presenter {
    String name
    def buttonClick() {
        if (name) {
            $('#salutes').append("<p>Hello ${name}!</p>")
        }
    }
}'''

    static final HTML_TEXT = '''<html>
<head>
    <title>Initial static web page</title>
    <script type="text/javascript" src="js/lib/jquery.min.js"></script>
    <script type="text/javascript" src="js/lib/grooscript.min.js"></script>
    <script type="text/javascript" src="js/lib/grooscript-tools.js"></script>
    <script type="text/javascript" src="js/app/Presenter.js"></script>
</head>
<body>
    <p>Name:<input type="text" id="name"/></p>
    <input type="button" id="button" value="Say hello!"/>
    <div id="salutes"/>
    <script type="text/javascript">
        presenter = Presenter();
        $(document).ready(function() {
            var gQuery = GQueryImpl();
            gQuery.bindAll(presenter);
            console.log('All binds done.');
        });
    </script>
</body>'''

    InitTools initTools

    @TaskAction
    def initStaticWeb() {
        if (initTools.existsFile(projectDir(HTML_FILE))) {
            throw new GradleException('Can\'t init static, files already generated.')
        } else {
            init()
        }
    }

    private init() {
        if (initTools.createDirs(projectDir(JS_LIB_DIR)) && initTools.createDirs(projectDir(JS_APP_DIR)) &&
            initTools.createDirs(projectDir(GROOVY_DIR)) &&
            initTools.saveFile(projectDir(HTML_FILE), HTML_TEXT) &&
            initTools.saveFile(projectDir(PRESENTER_FILE), PRESENTER_TEXT) &&
            initTools.extractGrooscriptJarFile(GROOSCRIPT_MIN_JS_NAME, projectDir(JS_LIB_DIR) + SEP + GROOSCRIPT_MIN_JS_NAME) &&
            initTools.extractGrooscriptJarFile(GROOSCRIPT_TOOLS_JS_NAME, projectDir(JS_LIB_DIR) + SEP + GROOSCRIPT_TOOLS_JS_NAME) &&
            initTools.saveRemoteFile(projectDir(JQUERY_JS_FILE), JQUERY_JS_REMOTE)) {
            println 'Generation completed.'
        } else {
            throw new GradleException('Error creating changes and dirs.')
        }
    }

    private String projectDir(String path) {
        project.projectDir.absolutePath + SEP + path
    }
}

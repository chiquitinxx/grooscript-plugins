package org.grooscript.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

import static org.grooscript.util.Util.SEP
/**
 * User: jorgefrancoleza
 * Date: 14/12/13
 */
class TemplatesTaskSpec extends Specification {

    def 'create the task'() {
        expect:
        task instanceof TemplatesTask
    }

    def 'default properties'() {
        when:
        def extension = new TemplatesExtension()
        project.extensions.templates.templatesPath = extension.templatesPath
        project.extensions.templates.templates = extension.templates
        project.extensions.templates.destinationFile = extension.destinationFile
        project.extensions.templates.classpath = extension.classpath
        project.extensions.templates.customTypeChecker = extension.customTypeChecker
        task.checkProperties()

        then:
        task.templatesPath != "src${SEP}main${SEP}webapp${SEP}templates"
        task.templatesPath.endsWith "src${SEP}main${SEP}webapp${SEP}templates"
        task.templates == null
        task.destinationFile != "src${SEP}main${SEP}webapp${SEP}js${SEP}lib${SEP}Templates.js"
        task.destinationFile.endsWith "src${SEP}main${SEP}webapp${SEP}js${SEP}lib${SEP}Templates.js"
        task.classpath.size() == 1
        task.classpath[0] != "src${SEP}main${SEP}groovy"
        task.classpath[0].endsWith "src${SEP}main${SEP}groovy"
        task.customTypeChecker == null
    }

    def 'generates templates needs variables setted'() {
        when:
        task.generateTemplatesJs()

        then:
        thrown(GradleException)

        when:
        task.templatesPath = 'src/test/resources'
        task.templates = ['one.gtpl']
        task.destinationFile = FILE_NAME
        task.generateTemplate()
        def generatedFile = new File(FILE_NAME)

        then:
        generatedFile.text.contains 'Templates.templates = gs.map().add("one.gtpl",function(model) {'
        generatedFile.text.contains 'return gs.mc(HtmlBuilder,"build",[function(it) {'
        generatedFile.text.contains 'return gs.mc(Templates,"p",["Hello!"]);'

        cleanup:
        if (generatedFile)
            generatedFile.delete()
    }

    @Unroll
    def 'templatesPath has to be a folder and destination has to be a .js file'() {
        given:
        task.templatesPath = 'src/test/resources'
        task.templates = ['one.gtpl']
        task.destinationFile = FILE_NAME

        when:
        task."$property" = value
        task.generateTemplate()

        then:
        thrown(GradleException)

        where:
        property          | value
        'templatesPath'   | 'build.gradle'
        'destinationFile' | 'build.gradle'
    }

    def 'generates templates with an include'() {
        when:
        task.templatesPath = "src${SEP}test${SEP}resources"
        task.templates = ['three.gtpl']
        task.destinationFile = FILE_NAME
        task.generateTemplate()
        def generatedFile = new File(FILE_NAME)

        then:
        generatedFile.text.contains "Templates,'applyTemplate'"

        cleanup:
        generatedFile.delete()
    }

    Project project
    TemplatesTask task
    private static final FILE_NAME = 'Templates.js'

    def setup() {
        project = ProjectBuilder.builder().build()
        project.extensions.templates = [:]

        task = project.task('templatesJs', type: TemplatesTask)
        task.project = project
    }
}

class OptionsResolver {

    File buildFile

    OptionsResolver(File buildFile) {
        this.buildFile = buildFile
    }

    void execute(@DelegatesTo(OptionsResolver)Closure options) {
        if (options) {
            options.delegate = this
            options()
        }
    }

    void addGrailsProjectDependency(String name) {
        buildFile.text += """
grails {
    plugins {
        compile project(':${name}')
    }
}
"""
    }

    void addDependency(String type, String text) {
        buildFile.text = buildFile.text.replaceFirst(/\ndependencies \{/,"""\ndependencies {
    ${type} \"${text}\"""")
    }

    void addBuildscriptDependency(String type, String text) {
        buildFile.text = buildFile.text.replace('classpath "org.grails:grails-gradle-plugin',
                """${type} \"${text}\"
        classpath "org.grails:grails-gradle-plugin""")
    }

    void comment(String text) {
        buildFile.text = buildFile.text.replaceAll(text, "//${text}")
    }

    void applyPlugin(String name) {
        buildFile.text = buildFile.text.replaceFirst(/\nrepositories \{/,
                """apply plugin:'${name}'

repositories {""")
    }
}

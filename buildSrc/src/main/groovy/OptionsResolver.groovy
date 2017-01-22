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
        buildFile.text = buildFile.text.replaceAll(/\ndependencies \{/,"""\ndependencies {
    ${type} \"${text}\"""")
    }

    void comment(String text) {
        buildFile.text = buildFile.text.replaceAll(text, "//${text}")
    }
}



import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class CreateGrailsApp extends DefaultTask {

    private static final String SEP = System.getProperty('file.separator')

    String sources
    String app
    String version = '3.2.4'
    Closure options

    @TaskAction
    def create() {
        if (sources && app && version) {
            File file = File.createTempFile("grails-$version", '.zip')
            downloadAndUnzipGrailsProject(file)
            copySourceFilesToGrailsProject(sources, app)
            File buildFile = new File(app + SEP + 'build.gradle')
            new OptionsResolver(buildFile).execute options
        } else {
            throw new GradleException("Need define 'sources' property with the folder that contains " +
                    "grails app sources. Also 'app' property with the app name.")
        }
    }

    private void downloadAndUnzipGrailsProject(File zipFile) {
        zipFile.withOutputStream { out ->
            out << new URL ("http://start.grails.org/generate?name=${app}&" +
                    "version=${version}&profile=web&features=asset-pipeline").openStream()
        }

        new File(app).deleteDir()
        new File(app).mkdirs()
        AntBuilder ant = new AntBuilder()
        ant.unzip(src: zipFile.absolutePath,
                dest: project.projectDir.absolutePath,
                overwrite: "true")
    }

    private void copySourceFilesToGrailsProject(String sourcePath, String projectPath) {
        AntBuilder ant = new AntBuilder()
        ant.copy(toDir: projectPath, overwrite: true) {
            fileset(dir: sourcePath) {
                include(name: '**/*')
            }
        }
    }
}

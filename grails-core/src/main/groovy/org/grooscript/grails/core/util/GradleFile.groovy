package org.grooscript.grails.core.util

class GradleFile implements FileSupport {

    String baseDir = ''

    String getFileContent(String path) {
        new File(baseDir + path).text
    }

    String getFolderPath(String path) {
        new File(baseDir + path).absolutePath
    }
}

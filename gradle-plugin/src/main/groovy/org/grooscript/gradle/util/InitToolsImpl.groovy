package org.grooscript.gradle.util

import org.grooscript.GrooScript

/**
 * Created by jorge on 16/02/14.
 */
class InitToolsImpl implements InitTools {
    boolean existsFile(String fileRelative) {
        def file = new File(fileRelative)
        file && file.exists() && file.isFile()
    }

    boolean createDirs(String dirRelative) {
        new File(dirRelative).mkdirs()
        true
    }

    boolean saveFile(String fileRelative, String content) {
        new File(fileRelative).text = content
        true
    }

    boolean extractGrooscriptJarFile(String fileName, String fileDestinationRelative) {
        new File(fileDestinationRelative).text =
            GrooScript.classLoader.getResourceAsStream('META-INF/resources/' + fileName).text
        true
    }

    boolean saveRemoteFile(String fileRelative, String url) {
        new File(fileRelative).text = url.toURL().text
        true
    }
}

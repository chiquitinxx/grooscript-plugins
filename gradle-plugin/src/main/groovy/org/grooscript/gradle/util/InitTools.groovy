package org.grooscript.gradle.util

/**
 * Created by jorge on 15/02/14.
 */
interface InitTools {
    boolean existsFile(String fileRelative)
    boolean createDirs(String dirRelative)
    boolean saveFile(String fileRelative, String content)
    boolean extractGrooscriptJarFile(String fileName, String fileDestinationRelative)
    boolean saveRemoteFile(String fileRelative, String url)
}

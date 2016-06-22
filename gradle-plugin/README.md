grooscript-gradle-plugin
===

Gradle plugin to convert your groovy files to javascript using grooscript. Last version published is 1.2.3.

build.gradle to use the plugin with gradle 2.0+ version:

<pre>
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'org.grooscript:grooscript-gradle-plugin:1.2.3'
    }
}

apply plugin: 'org.grooscript.conversion'
</pre>

build.gradle to use the plugin with gradle 2.1+:

<pre>
plugins {
  id "org.grooscript.conversion" version "1.2.3"
}
</pre>

There are 4 extension, convert files, convert templates, spy changes in files and generate require.js modules.

<pre>
//Convert files, you can change options
grooscript {
    source = ['src/main/groovy/presenters'] //Sources to be converted(List<String>), default is ['src/main/groovy']
    destination = 'js' //Target directory for js files, default is 'src/main/webapp/js/app'
    classpath = ['src/main/groovy'] //Needed classpath's to compile source files(List<String>), default is ['src/main/groovy']
    customization = null //Customization in files, it's a closure, as for example { -> ast(groovy.transform.TypeChecked) }
    initialText = '//Grooscript converted file'
    initialText = '//End converted file'
    recursive = true //Default is false
    mainContextScope = ['$'] //Variables available in main scope (List<String>), default is null
    addGsLib = 'grooscript' //Include a grooscript js lib in the result, default is null
    includeDependencies = true //Include conversion of dependency files, default is false
}

//Templates options
templates {
    templatesPath = 'src/main/webapp/templates'
    templates = ['main.gtpl', 'little/small.tpl']
    destinationFile = 'src/main/webapp/js/lib'
    classpath = ['src/main/groovy']
}

//Spy files changes
spy {
    files = ['anyFolder', 'aFile']
    onChanges = { list ->
        println 'Changes!! ' + list
    }
}

//Require.js modules
requireJs {
    sourceFile = 'src/main/groovy/MyApp.groovy'
    destinationFolder = 'src/main/resources/web/js/app'
}
</pre>

There are 9 tasks:

__convert__ - to convert groovy files to javascript

__convertThread__ - to run conversion daemon that detect file changes and convert the files to javascript

__initStaticWeb__ - create a static web project with index.html in src/main/webabb, to work with grooscript

__templatesJs__ - generate javascript file with groovy templates defined

__templatesThread__ - run daemon to convert templates. Executes daemon in a thread, so is perfect to use with other task

__spyChanges__ - listen changes in files

__syncGsLibs__ - synchronize grooscript libraries with plugin

__requireJs__ - to generate require.js modules from groovy code

__requireJsThread__ - listen changes in groovy code to regenerate require.js modules

More info about tasks [here](http://grooscript.org/gradle/tasks.html)

Guide about using this plugin [here](http://grooscript.org/starting_gradle.html)

Contributors
---

- Jorge Franco
- Michal Bernhard
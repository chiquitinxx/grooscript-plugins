Grooscript grails 3 plugin 1.3.0-SNAPSHOT
===

[![Build Status](https://snap-ci.com/chiquitinxx/grooscript-grails3-plugin/branch/master/build_image)](https://snap-ci.com/chiquitinxx/grooscript-grails3-plugin/branch/master)

It requires Grails 3.0+. Asset-pipeline and cache plugin are used to build this plugin, you need 
them in your application.

Use your groovy code in your gsp's. Your code, converted to javascript, will run in your browser.

Take a look at [documentation](http://grooscript.org/grails3-plugin/)

You can use the plugin with:

    compile "org.grails.plugins:grooscript:1.2.2"

You can use [gradle plugin](https://github.com/chiquitinxx/grooscript-gradle-plugin) to convert groovy 
files, templates,... and combine with this plugin to use in your gsp's.

More info about [grooscript](http://grooscript.org/)

Multi-project
---

Actually, there are 3 projects:

grails-core: library used by grooscript gradle plugin and this grails plugin
grails-plugin: this grails 3 plugin
test-app: a grails 3 app to test the plugin

TO-DO
---

- Remove the needed or add grooscript js libraries in the gsp's.
- Publish grails events to listening clients using websockets.
- Execute geb tests in sauce labs or something-similar.
- Publish grails-core in bintray and use it in grooscript gradle plugin.


Collaborators
---
Dilvan Moreira

Christian Meyer

Tepsl

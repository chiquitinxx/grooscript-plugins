Grooscript grails 3 plugin 1.0.0.M1
===

It requires Grails 3.0+. Asset-pipeline and cache plugin are used to build this plugin, you need them in your application.

Use your groovy code in your gsp's. Your code, converted to javascript, will run in your browser.

*Need create or update doc for Grails 3*

Tags are available, see [documentation](http://grooscript.org/grails-plugin/index.html)

After install, you can use with:

    compile "org.grails.plugins:grooscript:1.0.0.M1"

[Grooscript homepage](http://grooscript.org/)

Build
---

Generate grooscript javascript file with:

    groovy -cp src/main/groovy generateGrooscriptGrailsJs.groovy
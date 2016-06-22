Grooscript plugins
===

This is a multiproject gradle where the grooscript plugin sources are.

More info about [grooscript](http://grooscript.org/)

Multi-project
---

Actually, there are 4 projects:

grails-core: library used by grooscript gradle and grails plugins
grails-plugin: grooscript grails 3 plugin
gradle-plugin: grooscript gradle plugin
test-app: a grails 3 app to test the grails plugin

TO-DO
---

- Remove the needed or add grooscript js libraries in the gsp's.
- Publish grails events to listening clients using websockets.
- Execute geb tests in sauce labs or something-similar.
- Publish grails-core in bintray and use it in grooscript gradle plugin.

Build
---

To make all checks (included geb tests using firefox driver):

    ./gradlew check

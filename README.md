Grooscript plugins
===

[![Build Status](https://snap-ci.com/chiquitinxx/grooscript-plugins/branch/master/build_image)](https://snap-ci.com/chiquitinxx/grooscript-plugins/branch/master)
[![Build status](https://ci.appveyor.com/api/projects/status/rdu67y0p9fac50pu/branch/master?svg=true)](https://ci.appveyor.com/project/chiquitinxx/grooscript-plugins/branch/master)

This is a gradle multiproject where the grooscript gradle and grails plugins sources are.

More info about [grooscript](http://grooscript.org/)

Multi-project
---

Grails 3 build, there are 5 projects:

- grails-core: library used by grooscript gradle and grails plugins
- grails-plugin: grooscript grails 3 plugin(*)
- gradle-plugin: grooscript gradle plugin(*)
- test-app: a grails 3 app to test the grails plugin (build from test-app-sources with gradle task createTestApp )
- websockets-test-app: a grails 3 app to test the grails plugin websocket features (build from websockets-test-app-sources with gradle task createWebsocketsTestApp)

(*) published in [bintray](https://bintray.com/chiquitinxx/grooscript)

TO-DO
---

- Test components (how check final test? not in normal DOM).
- Update documentation.

Build
---

To make all checks (included geb tests using chrome remote driver if you built test apps):

    ./gradlew check
    
Test jar & components
---

To test jar generation with components, first test app must be created with:

    ./gradlew createTestApp
    
Later, run the test with:

    ./gradlew checkComponentInJar

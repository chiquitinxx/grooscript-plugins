Grooscript plugins
===

[![Build Status](https://snap-ci.com/chiquitinxx/grooscript-plugins/branch/master/build_image)](https://snap-ci.com/chiquitinxx/grooscript-plugins/branch/master)
[![Build status](https://ci.appveyor.com/api/projects/status/rdu67y0p9fac50pu/branch/master?svg=true)](https://ci.appveyor.com/project/chiquitinxx/grooscript-plugins/branch/master)

This is a multiproject gradle where the grooscript plugin sources are.

More info about [grooscript](http://grooscript.org/)

Multi-project
---

Actually, there are 4 projects:

- grails-core: library used by grooscript gradle and grails plugins
- grails-plugin: grooscript grails 3 plugin
- gradle-plugin: grooscript gradle plugin
- test-app: a grails 3 app to test the grails plugin

TO-DO
---

- Remove the needed or add grooscript js libraries in the gsp's.
- Publish grails events to listening clients using websockets.
- Execute geb tests in sauce labs or something-similar.
- In the gradle plugin, convert components and remote domain classes before jar / war is done in grails apps.

Build
---

To make all checks (included geb tests using firefox driver):

    ./gradlew check

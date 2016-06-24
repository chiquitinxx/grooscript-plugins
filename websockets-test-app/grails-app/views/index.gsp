<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Welcome to Grails</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>
    <p>Environment</p>
    <ul>
        <li>Environment: ${grails.util.Environment.current.name}</li>
        <li>App profile: ${grailsApplication.config.grails?.profile}</li>
        <li>App version:
            <g:meta name="info.app.version"/>
        </li>
        <li role="separator" class="divider"></li>
        <li>Grails version:
            <g:meta name="info.app.grailsVersion"/>
        </li>
        <li>Groovy version: ${GroovySystem.getVersion()}</li>
        <li>JVM version: ${System.getProperty('java.version')}</li>
        <li role="separator" class="divider"></li>
        <li>Reloading active: ${grails.util.Environment.reloadingAgentEnabled}</li>
    </ul>

    <p>Numbers</p>
    <ul class="dropdown-menu">
        <li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
        <li>Domains: ${grailsApplication.domainClasses.size()}</li>
        <li>Services: ${grailsApplication.serviceClasses.size()}</li>
        <li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
    </ul>

    <p>Plugins</p>
    <ul class="dropdown-menu">
        <g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins}">
            <li>${plugin.name} - ${plugin.version}</li>
        </g:each>
    </ul>

</body>
</html>

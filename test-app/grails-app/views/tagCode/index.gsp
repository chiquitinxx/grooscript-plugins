<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Tag code tests</title>
    <asset:javascript src="grooscript-grails.js"/>
</head>
<body>
    <asset:script>
        console.log("Assets ready!");
    </asset:script>
    <grooscript:code>
        def message = ['Hello', 'World!', 'from', 'grooscript :)'].join ' '
        document.body.innerHTML = message
    </grooscript:code>
</body>
</html>

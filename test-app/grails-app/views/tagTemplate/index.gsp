<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Tag template tests</title>
    <asset:javascript src="grooscript-grails.js"/>
</head>
<body>
    <div id="first">
        <grooscript:template>
            ul {
                5.times { number ->
                    li number + " li item"
                }
            }
        </grooscript:template>
    </div>
</body>
</html>

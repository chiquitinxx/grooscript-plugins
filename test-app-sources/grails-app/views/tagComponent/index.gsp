<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Tag component tests</title>
    <asset:javascript src="webcomponents.min.js"/>
</head>
<body>
    <grooscript:component src="component.Message" name="my-message"/>

    <my-message></my-message>

    <my-message message="Hello from grooscript!"></my-message>

    <my-message></my-message>

    <my-message></my-message>

</body>
</html>

<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Javascript json calls</title>
    <asset:javascript src="spring-websocket"/>
</head>
<body>
    <div id="first"></div>
    <grooscript:initSpringWebsocket>
        GrooscriptGrails.sendWebsocketMessage '/app/hello', 'World'
    </grooscript:initSpringWebsocket>
    <grooscript:onWebsocket path="/topic/hello">
        GsHlp.selectorHtml '#first', data
    </grooscript:onWebsocket>
</body>
</html>

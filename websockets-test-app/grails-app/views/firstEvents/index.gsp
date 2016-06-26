<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Javascript json calls</title>
    <asset:javascript src="grooscript-grails.js"/>
    <asset:javascript src="spring-websocket"/>
</head>
<body>
    <div id="first"></div>
    <div id="second"></div>
    <div id="third"></div>
    <div id="fourth"></div>
    <grooscript:code>
        def numberHappens = 0
    </grooscript:code>
    <grooscript:initSpringWebsocket>
        GrooscriptGrails.doRemoteCall 'firstEvents', 'doEvents', null, { result ->
            GsHlp.selectorHtml '#first', result
        }
        GrooscriptGrails.notifyEvent('somethingHappened', [one: 1, two: 'two'])
    </grooscript:initSpringWebsocket>
    <grooscript:onGrailsEvent name="hello">
        GsHlp.selectorHtml '#second', data
    </grooscript:onGrailsEvent>
    <grooscript:onGrailsEvent name="gotMap">
        GsHlp.selectorHtml '#third', data
    </grooscript:onGrailsEvent>
    <grooscript:onGrailsEvent name="somethingHappened">
        println data
        numberHappens = numberHappens + 1
        GsHlp.selectorHtml '#fourth', numberHappens
    </grooscript:onGrailsEvent>
</body>
</html>

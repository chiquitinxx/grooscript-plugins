<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Javascript json calls</title>
    <asset:javascript src="spring-websocket"/>
</head>
<body>
    <div id="first"></div>
    <div id="second"></div>
    <div id="third"></div>
    <div id="fourth"></div>
    <div id="errors"></div>
    <div id="messages"></div>
    <grooscript:code>
        def numberHappens = 0
        def jsErrors = []
        def addJsError = { error ->
            jsErrors << error
            GsHlp.selectorHtml '#errors', jsErrors
        }
        window.onerror = addJsError
    </grooscript:code>
    <grooscript:initSpringWebsocket>
        GrooscriptGrails.doRemoteCall 'firstEvents', 'doEvents', null, { result ->
            GsHlp.selectorHtml '#first', result
        }, { error -> addJsError(error) }
        GsHlp.appendHtml '#messages', '<p>follow</p>'
        GrooscriptGrails.notifyEvent('somethingHappened', 'First!')
    </grooscript:initSpringWebsocket>
    <grooscript:onGrailsEvent name="hello">
        GsHlp.selectorHtml '#second', data
        GrooscriptGrails.notifyEvent('somethingHappened', 'Second')
    </grooscript:onGrailsEvent>
    <grooscript:onGrailsEvent name="gotmap">
        GsHlp.selectorHtml '#third', data.b
        GrooscriptGrails.notifyEvent('somethingHappened', 'Third!')
    </grooscript:onGrailsEvent>
    <grooscript:onGrailsEvent name="somethingHappened">
        numberHappens = numberHappens + 1
        GsHlp.selectorHtml '#fourth', numberHappens
    </grooscript:onGrailsEvent>
</body>
</html>

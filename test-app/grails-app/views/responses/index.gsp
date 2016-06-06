<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Javascript json calls</title>
    <asset:javascript src="grooscript-grails.js"/>
</head>
<body>
    <div id="first"></div>
    <div id="second"></div>
    <div id="third"></div>
    <grooscript:code>
        import org.grooscript.grails.core.util.GrooscriptGrails

        GrooscriptGrails.doRemoteCall 'responses', 'simpleJson', null, { result ->
            GsHlp.selectorHtml '#first', result.action + ' ' + result.who + ' -> ' + result.times
        }
        GrooscriptGrails.doRemoteCall 'responses', 'text', null, { result ->
            GsHlp.selectorHtml '#second', result
        }
        GrooscriptGrails.doRemoteCall 'responses', 'empty', null, { result ->
            GsHlp.selectorHtml '#third', result ?: 'isEmpty'
        }
    </grooscript:code>
</body>
</html>

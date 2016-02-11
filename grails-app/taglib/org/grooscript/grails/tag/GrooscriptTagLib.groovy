package org.grooscript.grails.tag

import grails.core.GrailsApplication
import grails.util.GrailsUtil
import grails.web.mapping.LinkGenerator
import org.grooscript.grails.Templates
import org.grooscript.grails.bean.GrooscriptConverter
import org.grooscript.grails.util.GrooscriptTemplate
import org.grooscript.grails.util.Util

import static org.grooscript.grails.util.Util.*

class GrooscriptTagLib {

    static final REMOTE_URL_SETTED = 'grooscriptRemoteUrl'
    static final WEBSOCKET_STARTED = 'grooscriptWebsocketStarted'
    static final REMOTE_DOMAIN_EXTENSION = '.gs'
    static final COMPONENT_EXTENSION = '.cs'

    static namespace = 'grooscript'

    GrailsApplication grailsApplication
    GrooscriptConverter grooscriptConverter
    LinkGenerator grailsLinkGenerator
    GrooscriptTemplate grooscriptTemplate

    /**
     * grooscript:code
     * conversionOptions - optional - map of conversion options
     */
    def code = { attrs, body ->
        def script
        script = body()
        if (script) {
            initGrooscriptGrails()
            def jsCode = grooscriptConverter.toJavascript(script.toString(), attrs.conversionOptions)
            asset.script(type: 'text/javascript') {
                jsCode
            }
        }
    }

    /**
     * grooscript: template
     *
     * functionName - optional - name of the function that renders the template
     * itemSelector - optional - jQuery string selector where html generated will be placed
     * onLoad - optional defaults true - if template will be render onReady page event
     * onEvent - optional - string list of events that render the page
     */
    def template = { attrs, body ->
        def script
        script = body()
        if (script) {
            def functionName = attrs.functionName ?: newTemplateName
            String jsCode = grooscriptConverter.toJavascript("def gsTextHtml = { data -> HtmlBuilder.build { builderIt -> ${script}}}").trim()

            initGrooscriptGrails()

            if (!attrs.itemSelector) {
                out << "\n<div id='${functionName}'></div>\n"
            }

            asset.script(type: 'text/javascript') {
                def result = grooscriptTemplate.apply(Templates.TEMPLATE_DRAW, [
                        functionName: functionName,
                        jsCode: jsCode,
                        selector: attrs.itemSelector ? attrs.itemSelector : "#${functionName}"
                ])
                if (attrs['onLoad'] == null || attrs['onLoad'] == 'true' || attrs['onLoad'] == true) {
                    result += grooscriptTemplate.apply(Templates.TEMPLATE_ON_READY, [functionName: functionName])
                }
                result
            }

            processTemplateEvents(attrs.onEvent, functionName)
        }
    }

    /**
     * grooscript:remoteModel
     * domainClass - REQUIRED name of the model class
     */
    def remoteModel = { attrs ->
        if (validDomainClassName(attrs.domainClass)) {
            initGrooscriptGrails()
            out << asset.script(type: 'text/javascript') {
                GrailsUtil.isDevelopmentEnv() ?
                    grooscriptConverter.convertRemoteDomainClass(getDomainClassCanonicalName(attrs.domainClass)) :
                    Util.getResourceText(getShortName(attrs.domainClass) + REMOTE_DOMAIN_EXTENSION)
            }
        }
    }

    /**
     * grooscript:onEvent
     * name - name of the event
     */
    def onEvent = { attrs, body ->
        String name = attrs.name
        if (name) {
            initGrooscriptGrails()

            def script = body()
            def jsCode = grooscriptConverter.toJavascript("{ event -> ${script}}").trim()

            asset.script(type: 'text/javascript') {
                grooscriptTemplate.apply(Templates.ON_EVENT_TAG,
                        [jsCode: removeLastSemicolon(jsCode), nameEvent: name])
            }

        } else {
            consoleError 'GrooscriptTagLib onEvent need define name property'
        }
    }

    /**
     * grooscript:initSpringWebsocket
     */
    def initSpringWebsocket = { attrs, body ->

        def script = body()
        def jsCode = ''
        def endPoint = attrs.endPoint ?: '/stomp'
        if (script) {
            jsCode = grooscriptConverter.toJavascript(script)
        }
        initGrooscriptGrails()
        String withDebugString = attrs.withDebug
        boolean withDebug = withDebugString == null ? false : new Boolean(withDebugString)
        asset.script(type: 'text/javascript') {
            grooscriptTemplate.apply(Templates.SPRING_WEBSOCKET,
                    [endPoint: endPoint, jsCode: jsCode, withDebug: withDebug])
        }
        request.setAttribute(WEBSOCKET_STARTED, true)
    }

    /**
     * grooscript:onServerEvent
     */
    def onServerEvent = { attrs, body ->

        def script = body()
        def jsCode = ''
        initWebsocket()
        if (script) {
            jsCode = grooscriptConverter.toJavascript(
                    grooscriptTemplate.apply(Templates.ON_SERVER_EVENT_RUN, [code: script]))
        }
        def functionName = onServerEventFunctionName
        asset.script(type: 'text/javascript') {
            grooscriptTemplate.apply(Templates.ON_SERVER_EVENT,
                    [jsCode: jsCode, path: attrs.path,
                     functionName: functionName,
                     type: attrs.type ?: 'null'])
        }
    }

    /**
     * grooscript:reloadOn
     */
    def reloadOn = { attrs, body ->

        if (GrailsUtil.isDevelopmentEnv()) {
            String path = attrs.path
            if (path) {
                initWebsocket()
                def functionName = onServerEventFunctionName
                asset.script(type: 'text/javascript') {
                    grooscriptTemplate.apply(Templates.RELOAD_ON,
                            [path        : path,
                             functionName: functionName])
                }
            } else {
                consoleError 'GrooscriptTagLib reloadOn need define path property'
            }
        }
    }

    /**
     * grooscript:component
     */
    def component = { attrs, body ->
        def fullClassName = attrs.src
        if (fullClassName) {
            String shortClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1)
            String nameComponent = attrs.name ?: componentName(shortClassName)
            if (GrailsUtil.isDevelopmentEnv()) {
                def source = getClassSource(fullClassName)
                def componentJs = grooscriptConverter.convertComponent(source)
                out << asset.script(type: 'text/javascript') {
                    componentJs + ";GrooscriptGrails.createComponent(${shortClassName}, '${nameComponent}');"
                }
            } else {
                out << asset.script(type: 'text/javascript') {
                    Util.getResourceText(shortClassName + COMPONENT_EXTENSION) +
                            ";GrooscriptGrails.createComponent(${shortClassName}, '${nameComponent}');"
                }
            }
        }
    }

    private void initGrooscriptGrails() {
        def urlSetted = request.getAttribute(REMOTE_URL_SETTED)
        if (!urlSetted) {
            asset.script(type: 'text/javascript') {
                grooscriptTemplate.apply(Templates.INIT_GROOSCRIPT_GRAILS,
                        [remoteUrl: grailsLinkGenerator.serverBaseURL])
            }
            request.setAttribute(REMOTE_URL_SETTED, true)
        }
    }

    private void initWebsocket() {
        def websocketStarted = request.getAttribute(WEBSOCKET_STARTED)
        if (!websocketStarted) {
            initSpringWebsocket([:], { -> ''})
        }
    }

    private String getDomainClassCanonicalName(String domainClass) {
        domainClassFromName(domainClass)?.clazz?.canonicalName
    }

    private String getShortName(String domainClass) {
        domainClass.split('\\.').last()
    }

    private void processTemplateEvents(String onEvent, functionName) {
        if (onEvent) {
            def listEvents
            if (onEvent.contains(',')) {
                listEvents = onEvent.split(',')
            } else {
                listEvents = [onEvent]
            }
            listEvents.each { nameEvent ->
                asset.script(type: 'text/javascript') {
                    grooscriptTemplate.apply(Templates.CLIENT_EVENT,
                            [functionName: functionName, nameEvent: nameEvent.trim()])
                }
            }
        }
    }

    private boolean validDomainClassName(String name) {
        if (!name || !(name instanceof String)) {
            consoleError "GrooscriptTagLib have to define domainClass property as String"
        } else {
            if (domainClassFromName(name)) {
                return true
            } else {
                consoleError "Not exist domain class ${name}"
            }
        }
        return false
    }

    private domainClassFromName(String nameClass) {
        grailsApplication.getDomainClasses().find { it.fullName == nameClass || it.name == nameClass }
    }

    private String removeLastSemicolon(String code) {
        if (code.lastIndexOf(';') >= 0) {
            return code.substring(0, code.lastIndexOf(';'))
        } else {
            return code
        }
    }

    private static final ON_SERVER_EVENT_FUNCTION_NAME = 'gSonServerEvent'
    private static final ON_SERVER_EVENT_COUNT = 'gSonEventCount'

    private String getOnServerEventFunctionName() {
        def number = request.getAttribute(ON_SERVER_EVENT_COUNT)
        if (number == null) {
            number = 0
        }
        request.setAttribute(ON_SERVER_EVENT_COUNT, number + 1)
        ON_SERVER_EVENT_FUNCTION_NAME + number
    }

    private String componentName(String className) {
        def name = className.substring(0, 1).toLowerCase() + className.substring(1)
        while (hasUpperCase(name)) {
            name = rplaceFirstUpperCase(name)
        }
        name
    }

    private boolean hasUpperCase(String name) {
        boolean hasUpper = false
        def i
        for (i = 0; i < name.length() && !hasUpper; i++) {
            if (name.charAt(i).upperCase) {
                hasUpper = true
            }
        }
        hasUpper
    }

    private String rplaceFirstUpperCase(String name) {
        def upper = name.find {
            it.charAt(0).upperCase
        }
        if (upper) {
            name = name.replaceFirst(upper, '-' + upper.toLowerCase())
        }
        name
    }
}

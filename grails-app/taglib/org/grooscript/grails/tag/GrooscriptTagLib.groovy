package org.grooscript.grails.tag

import grails.core.GrailsApplication
import grails.util.GrailsUtil
import grails.web.mapping.LinkGenerator
import org.grooscript.grails.Templates
import org.grooscript.grails.bean.GrooscriptConverter
import org.grooscript.grails.util.GrooscriptTemplate

import static org.grooscript.grails.util.Util.*

class GrooscriptTagLib {

    static final String REMOTE_URL_SETTED = 'grooscriptRemoteUrl'
    static final String WEBSOCKET_STARTED = 'grooscriptWebsocketStarted'
    static final String REMOTE_DOMAIN_EXTENSION = '.gs'
    static final String COMPONENT_EXTENSION = '.cs'

    static final String namespace = 'grooscript'

    GrailsApplication grailsApplication
    GrooscriptConverter grooscriptConverter
    LinkGenerator grailsLinkGenerator
    GrooscriptTemplate grooscriptTemplate

    /**
     * grooscript:code
     * conversionOptions - optional - map of conversion options
     */
    def code = { Map attrs, Closure<String> body ->
        String script = body()
        if (script) {
            initGrooscriptGrails()
            String jsCode = grooscriptConverter.toJavascript(script, attrs.conversionOptions as Map)
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
    def template = { Map attrs, Closure<String> body ->
        String script = body()
        if (script) {
            String functionName = attrs.functionName ?: newTemplateName
            String jsCode = grooscriptConverter.toJavascript("def gsTextHtml = { data -> HtmlBuilder.build { builderIt -> ${script}}}").trim()

            initGrooscriptGrails()

            if (!attrs.itemSelector) {
                out << "\n<div id='${functionName}'></div>\n"
            }

            asset.script(type: 'text/javascript') {
                String result = grooscriptTemplate.apply(
                        Templates.TEMPLATE_DRAW,
                        [functionName: functionName,
                        jsCode: jsCode,
                        selector: attrs.itemSelector ? attrs.itemSelector : "#${functionName}"
                ])

                if (attrs['onLoad'] == null || attrs['onLoad'] == 'true' || attrs['onLoad'] == true) {
                    result += grooscriptTemplate.apply(
                            Templates.TEMPLATE_ON_READY, [functionName: functionName])
                }
                result
            }

            processTemplateEvents(attrs.onEvent as String, functionName)
        }
    }

    /**
     * grooscript:remoteModel
     * domainClass - REQUIRED name of the model class
     */
    def remoteModel = { Map attrs ->
        String domainClass = attrs.domainClass as String
        if (validDomainClassName(domainClass)) {
            initGrooscriptGrails()

            out << asset.script(type: 'text/javascript') {
                GrailsUtil.isDevelopmentEnv() ? grooscriptConverter.convertRemoteDomainClass(getDomainClassCanonicalName(domainClass))
                        : getResourceText(getShortName(domainClass) + REMOTE_DOMAIN_EXTENSION)
            }
        }
    }

    /**
     * grooscript:onEvent
     * name - name of the event
     */
    def onEvent = { Map attrs, Closure<String> body ->
        String name = attrs.name
        if (name) {
            initGrooscriptGrails()

            String script = body()
            String jsCode = grooscriptConverter.toJavascript("{ event -> ${script}}").trim()

            asset.script(type: 'text/javascript') {
                grooscriptTemplate.apply(
                        Templates.ON_EVENT_TAG,
                        [jsCode: removeLastSemicolon(jsCode), nameEvent: name])
            }

        } else {
            consoleError 'GrooscriptTagLib onEvent need define name property'
        }
    }

    /**
     * grooscript:initSpringWebsocket
     */
    def initSpringWebsocket = { Map attrs, Closure<String> body ->
        String script = body()
        String jsCode = script ? grooscriptConverter.toJavascript(script) : ''

        initGrooscriptGrails()

        String endPoint = attrs.endPoint ?: '/stomp'
        String withDebugString = attrs.withDebug
        boolean withDebug = withDebugString == null ? false : Boolean.valueOf(withDebugString)

        asset.script(type: 'text/javascript') {
            grooscriptTemplate.apply(
                    Templates.SPRING_WEBSOCKET,
                    [endPoint: endPoint, jsCode: jsCode, withDebug: withDebug])
        }
        request.setAttribute(WEBSOCKET_STARTED, true)
    }

    /**
     * grooscript:onServerEvent
     */
    def onServerEvent = { Map attrs, Closure<String> body ->
        String script = body()
        initWebsocket()
        String jsCode = script ? grooscriptConverter.toJavascript(
                grooscriptTemplate.apply(Templates.ON_SERVER_EVENT_RUN, [code: script])) : ''

        asset.script(type: 'text/javascript') {
            grooscriptTemplate.apply(
                    Templates.ON_SERVER_EVENT,
                    [jsCode: jsCode,
                     path: attrs.path,
                     functionName: onServerEventFunctionName,
                     type: attrs.type ?: 'null'])
        }
    }

    /**
     * grooscript:reloadOn
     */
    def reloadOn = { Map attrs, Closure<String> body ->
        if (GrailsUtil.isDevelopmentEnv()) {
            String path = attrs.path
            if (path) {
                initWebsocket()
                asset.script(type: 'text/javascript') {
                    grooscriptTemplate.apply(
                            Templates.RELOAD_ON,
                            [path: path, functionName: onServerEventFunctionName])
                }
            } else {
                consoleError 'GrooscriptTagLib reloadOn need define path property'
            }
        }
    }

    /**
     * grooscript:component
     */
    def component = { Map attrs, Closure<String> body ->
        String fullClassName = attrs.src
        if (fullClassName) {
            initGrooscriptGrails()
            String shortClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1)
            String nameComponent = attrs.name ?: componentName(shortClassName)
            if (GrailsUtil.isDevelopmentEnv()) {
                String source = getClassSource(fullClassName)
                String componentJs = grooscriptConverter.convertComponent(source)
                out << asset.script(type: 'text/javascript') {
                    componentJs + ";GrooscriptGrails.createComponent(${shortClassName}, '${nameComponent}');"
                }
            } else {
                String resourceText = getResourceText(shortClassName + COMPONENT_EXTENSION)
                out << asset.script(type: 'text/javascript') {
                    resourceText + ";GrooscriptGrails.createComponent(${shortClassName}, '${nameComponent}');"
                }
            }
        }
    }

    private void initGrooscriptGrails() {
        def urlSet = request.getAttribute(REMOTE_URL_SETTED)
        if (!urlSet) {
            asset.script(type: 'text/javascript') {
                grooscriptTemplate.apply(
                        Templates.INIT_GROOSCRIPT_GRAILS,
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

    private static String getShortName(String domainClass) {
        domainClass.split('\\.').last()
    }

    private void processTemplateEvents(String onEvent, String functionName) {
        if (onEvent) {
            List listEvents = onEvent.contains(',') ? onEvent.split(',') as List : [onEvent]

            listEvents.each { String nameEvent ->
                asset.script(type: 'text/javascript') {
                    grooscriptTemplate.apply(
                            Templates.CLIENT_EVENT,
                            [functionName: functionName, nameEvent: nameEvent.trim()]
                    )
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

    private String domainClassFromName(String nameClass) {
        grailsApplication.getDomainClasses().find { it.fullName == nameClass || it.name == nameClass }
    }

    private static String removeLastSemicolon(String code) {
        return code.lastIndexOf(';') >= 0 ? code.substring(0, code.lastIndexOf(';')) : code
    }

    private static final String ON_SERVER_EVENT_FUNCTION_NAME = 'gSonServerEvent'
    private static final String ON_SERVER_EVENT_COUNT = 'gSonEventCount'

    private String getOnServerEventFunctionName() {
        def number = request.getAttribute(ON_SERVER_EVENT_COUNT)
        if (number == null) {
            number = 0
        }
        request.setAttribute(ON_SERVER_EVENT_COUNT, number + 1)
        ON_SERVER_EVENT_FUNCTION_NAME + number
    }

    private static String componentName(String className) {
        String name = className.substring(0, 1).toLowerCase() + className.substring(1)
        while (hasUpperCase(name)) {
            name = replaceFirstUpperCase(name)
        }
        name
    }

    private static boolean hasUpperCase(String name) {
        boolean hasUpper = false
        for (int i = 0; i < name.length() && !hasUpper; i++) {
            if (name.substring(0, 1).toUpperCase()) {
                hasUpper = true
            }
        }
        hasUpper
    }

    private static String replaceFirstUpperCase(String name) {
        String upper = name.find { String it ->
            it.substring(0, 1).toUpperCase()
        }

        upper ? name.replaceFirst(upper, '-' + upper.toLowerCase()) : name
    }
}

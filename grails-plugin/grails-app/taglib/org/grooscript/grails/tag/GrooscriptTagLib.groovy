package org.grooscript.grails.tag

import grails.core.GrailsApplication
import grails.util.GrailsNameUtils
import grails.util.GrailsUtil
import grails.web.mapping.LinkGenerator
import org.grooscript.grails.Templates
import org.grooscript.grails.bean.JavascriptConverter
import org.grooscript.grails.util.GrailsHelpers
import org.grooscript.grails.util.JavascriptTemplate
import org.grooscript.grails.util.Util

import javax.annotation.ParametersAreNonnullByDefault

import static grails.util.Environment.isDevelopmentEnvironmentAvailable

/**
 * @author Jorge Franco <jorge.franco@osoco.es>
 */
@ParametersAreNonnullByDefault
class GrooscriptTagLib {

    static namespace = 'grooscript'

    private static final String WEBSOCKET_STARTED = 'grooscriptWebsocketStarted'

    private boolean sourceCodeAvailable = developmentEnvironmentAvailable

    GrailsApplication grailsApplication
    JavascriptConverter grooscriptConverter
    LinkGenerator grailsLinkGenerator
    JavascriptTemplate grooscriptTemplate
    GrailsHelpers grailsHelpers

    /**
     * grooscript:code
     * conversionOptions - optional - map of conversion options
     */
    def code = { attrs, body ->
        String script = body()
        if (script) {
            grailsHelpers.initGrooscriptGrails(request, asset, out)
            String jsCode = grooscriptConverter.toJavascript(script, attrs.conversionOptions as Map)
            grailsHelpers.addAssetScript(asset, out, jsCode)
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
        String script = body()
        if (!script) {
            return
        }

        String functionName = attrs.functionName ?: Util.newTemplateName
        String jsCode = grooscriptConverter.toJavascript("def gsTextHtml = { data -> HtmlBuilder.build { builderIt -> ${script}}}").trim()

        grailsHelpers.initGrooscriptGrails(request, asset, out)

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
                        Templates.TEMPLATE_ON_READY,
                        [functionName: functionName])
            }
            result
        }

        processTemplateEvents(attrs.onEvent as String, functionName)
    }

    /**
     * grooscript:remoteModel
     * domainClass - REQUIRED full name of the model class
     */
    def remoteModel = { attrs ->
        String domainClass = attrs.domainClass as String
        if (grailsHelpers.validDomainClassName(domainClass)) {
            grailsHelpers.initGrooscriptGrails(request, asset, out)
            String convertedDomainClass = grooscriptConverter.convertRemoteDomainClass(domainClass)
            grailsHelpers.addAssetScript(asset, out, convertedDomainClass)
        } else {
            Util.consoleError 'GrooscriptTagLib remoteModel need define valid domainClass property'
        }
    }

    /**
     * grooscript:onEvent
     * name - name of the event
     */
    def onEvent = { attrs, body ->
        String name = attrs.name
        if (name) {
            grailsHelpers.initGrooscriptGrails(request, asset, out)

            String script = body()
            String jsCode = grooscriptConverter.toJavascript("{ event -> ${script}}").trim()
            String jsCodeWithoutLastSemicolon = Util.removeLastSemicolon(jsCode)

            asset.script(type: 'text/javascript') {
                grooscriptTemplate.apply(
                        Templates.ON_EVENT_TAG,
                        [jsCode: jsCodeWithoutLastSemicolon, nameEvent: name])
            }

        } else {
            Util.consoleError 'GrooscriptTagLib onEvent need define name property'
        }
    }

    /**
     * grooscript:initSpringWebsocket
     */
    def initSpringWebsocket = { attrs, body ->
        String script = body()
        String jsCode = script ? grooscriptConverter.toJavascript(script) : ''

        grailsHelpers.initGrooscriptGrails(request, asset, out)

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
    def onServerEvent = { attrs, body ->
        String script = body()
        initWebsocket()

        String template = grooscriptTemplate.apply(Templates.ON_SERVER_EVENT_RUN, [code: script])
        String jsCode = script ? grooscriptConverter.toJavascript(template) : ''
        String functionName = onServerEventFunctionName

        asset.script(type: 'text/javascript') {
            grooscriptTemplate.apply(
                    Templates.ON_SERVER_EVENT,
                    [jsCode: jsCode,
                     path: attrs.path,
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

                String functionName = onServerEventFunctionName
                asset.script(type: 'text/javascript') {
                    grooscriptTemplate.apply(
                            Templates.RELOAD_ON,
                            [path: path, functionName: functionName])
                }
            } else {
                Util.consoleError 'GrooscriptTagLib reloadOn need define path property'
            }
        }
    }

    /**
     * grooscript:component
     */
    def component = { attrs, body ->
        String fullClassName = attrs.src
        if (fullClassName) {
            grailsHelpers.initGrooscriptGrails(request, asset, out)

            String shortClassName = GrailsNameUtils.getShortName(fullClassName)
            String nameComponent = attrs.name ?: GrailsNameUtils.getScriptName(shortClassName)
            String convertedComponent =
                    grooscriptConverter.getComponentCodeConverted(fullClassName, shortClassName, nameComponent)

            grailsHelpers.addAssetScript(asset, out, convertedComponent)
        }
    }

    private void initWebsocket() {
        def websocketStarted = request.getAttribute(WEBSOCKET_STARTED)
        if (!websocketStarted) {
            initSpringWebsocket([:], { -> ''})
        }
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
}
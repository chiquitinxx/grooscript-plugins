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
    static final REMOTE_DOMAIN_EXTENSION = '.gs'

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
        if (script) {
            jsCode = grooscriptConverter.toJavascript(script)
        }
        initGrooscriptGrails()
        boolean withDebug = attrs.withDebug == null ? false : attrs.withDebug
        def url = createLink(uri: '/stomp')
        asset.script(type: 'text/javascript') {
            grooscriptTemplate.apply(Templates.SPRING_WEBSOCKET,
                    [url: url, jsCode: jsCode, withDebug: withDebug])
        }
    }

    /**
     * grooscript:onServerEvent
     */
    def onServerEvent = { attrs, body ->

        def script = body()
        def jsCode = ''
        if (script) {
            jsCode = grooscriptConverter.toJavascript(
                    grooscriptTemplate.apply(Templates.ON_SERVER_EVENT_RUN, [code: script]))
        }
        asset.script(type: 'text/javascript') {
            grooscriptTemplate.apply(Templates.ON_SERVER_EVENT,
                    [jsCode: jsCode, path: attrs.path,
                     functionName: onServerEventFunctionName,
                     type: attrs.type ?: 'null'])
        }
    }

    private initGrooscriptGrails() {
        def urlSetted = request.getAttribute(REMOTE_URL_SETTED)
        if (!urlSetted) {
            asset.script(type: 'text/javascript') {
                grooscriptTemplate.apply(Templates.INIT_GROOSCRIPT_GRAILS,
                        [remoteUrl: grailsLinkGenerator.serverBaseURL])
            }
            request.setAttribute(REMOTE_URL_SETTED, true)
        }
    }

    private String getDomainClassCanonicalName(String domainClass) {
        domainClassFromName(domainClass)?.clazz?.canonicalName
    }

    private String getShortName(String domainClass) {
        domainClass.split('\\.').last()
    }

    private processTemplateEvents(String onEvent, functionName) {
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

    private validDomainClassName(String name) {
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

    private removeLastSemicolon(String code) {
        if (code.lastIndexOf(';') >= 0) {
            return code.substring(0, code.lastIndexOf(';'))
        } else {
            return code
        }
    }

    private static final ON_SERVER_EVENT_FUNCTION_NAME = 'gSonServerEvent'
    private static final ON_SERVER_EVENT_COUNT = 'gSonEventCount'

    private getOnServerEventFunctionName() {
        def number = request.getAttribute(ON_SERVER_EVENT_COUNT)
        if (number == null) {
            number = 0
        }
        request.setAttribute(ON_SERVER_EVENT_COUNT, number + 1)
        ON_SERVER_EVENT_FUNCTION_NAME + number
    }
}

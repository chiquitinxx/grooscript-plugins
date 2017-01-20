/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.grails.tag

import grails.core.GrailsApplication
import grails.util.GrailsNameUtils
import grails.util.GrailsUtil
import grails.web.mapping.LinkGenerator
import org.grooscript.grails.Templates
import org.grooscript.grails.bean.JavascriptConverter
import org.grooscript.grails.event.GrooscriptEventsInterceptor
import org.grooscript.grails.util.GrooscriptGrailsHelpers
import org.grooscript.grails.util.JavascriptTemplate
import org.grooscript.grails.util.Util

import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class GrooscriptTagLib {

    static namespace = 'grooscript'

    private static final String WEBSOCKET_STARTED = 'grooscriptWebsocketStarted'

    GrailsApplication grailsApplication
    JavascriptConverter javascriptConverter
    LinkGenerator grailsLinkGenerator
    JavascriptTemplate javascriptTemplate
    GrooscriptGrailsHelpers grooscriptGrailsHelpers
    GrooscriptEventsInterceptor grooscriptEventsInterceptor

    /**
     * grooscript:code
     * conversionOptions - optional - map of conversion options
     */
    def code = { attrs, body ->
        String script = body()
        if (script) {
            grooscriptGrailsHelpers.initGrooscriptGrails(request, asset, out)
            String jsCode = javascriptConverter.toJavascript(script, attrs.conversionOptions as Map)
            grooscriptGrailsHelpers.addAssetScript(asset, out, jsCode)
        }
    }

    /**
     * grooscript: template
     *
     * functionName - optional - name of the function that renders the template
     * itemSelector - optional - jQuery string selector where html generated will be placed
     * onLoad - optional defaults true - if template will be render onReady page event
     */
    def template = { attrs, body ->
        String script = body()
        if (!script) {
            return
        }

        String functionName = attrs.functionName ?: Util.newTemplateName
        String jsCode = javascriptConverter.toJavascript("def gsTextHtml = { data -> HtmlBuilder.build { builderIt -> ${script}}}").trim()

        grooscriptGrailsHelpers.initGrooscriptGrails(request, asset, out)

        if (!attrs.itemSelector) {
            out << "\n<div id='${functionName}'></div>\n"
        }

        asset.script(type: 'text/javascript') {
            String result = javascriptTemplate.apply(
                    Templates.TEMPLATE_DRAW,
                    [functionName: functionName,
                     jsCode: jsCode,
                     selector: attrs.itemSelector ? attrs.itemSelector : "#${functionName}"
                    ])

            if (attrs['onLoad'] == null || attrs['onLoad'] == 'true' || attrs['onLoad'] == true) {
                result += javascriptTemplate.apply(
                        Templates.TEMPLATE_ON_READY,
                        [functionName: functionName])
            }
            result
        }
    }

    /**
     * grooscript:initSpringWebsocket
     */
    def initSpringWebsocket = { attrs, body ->
        String script = body()
        String jsCode = script ? javascriptConverter.toJavascript(script) : ''

        grooscriptGrailsHelpers.initGrooscriptGrails(request, asset, out)

        String endPoint = attrs.endPoint ?: '/stomp'
        String withDebugString = attrs.withDebug
        boolean withDebug = withDebugString == null ? false : Boolean.valueOf(withDebugString)
        String wsPrefix = grooscriptGrailsHelpers.websocketDestinationPrefix

        asset.script(type: 'text/javascript') {
            javascriptTemplate.apply(
                    Templates.SPRING_WEBSOCKET,
                    [endPoint: endPoint, jsCode: jsCode, withDebug: withDebug, wsPrefix: wsPrefix])
        }
        request.setAttribute(WEBSOCKET_STARTED, true)
    }

    /**
     * grooscript:onGrailsEvent
     */
    def onGrailsEvent = { attrs, body ->
        String script = body()
        initWebsocket()

        String template = javascriptTemplate.apply(Templates.ON_SERVER_EVENT_RUN, [code: script])
        String jsCode = script ? javascriptConverter.toJavascript(template) : ''
        String functionName = onServerEventFunctionName
        String eventName = attrs.name
        String eventPath = grooscriptGrailsHelpers.grooscriptWebsocketTopicPrefix + eventName

        asset.script(type: 'text/javascript') {
            javascriptTemplate.apply(
                    Templates.ON_SERVER_EVENT,
                    [jsCode: jsCode,
                     path: eventPath,
                     functionName: functionName,
                     type: attrs.type ?: 'null'])
        }
        grooscriptEventsInterceptor.addEventToIntercept(eventName)
    }

    /**
     * grooscript:onWebsocket
     */
    def onWebsocket = { attrs, body ->
        String script = body()
        initWebsocket()

        String template = javascriptTemplate.apply(Templates.ON_SERVER_EVENT_RUN, [code: script])
        String jsCode = script ? javascriptConverter.toJavascript(template) : ''
        String functionName = onServerEventFunctionName
        String path = attrs.path

        asset.script(type: 'text/javascript') {
            javascriptTemplate.apply(
                    Templates.ON_SERVER_EVENT,
                    [jsCode: jsCode,
                     path: path,
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
                    javascriptTemplate.apply(
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
            grooscriptGrailsHelpers.initGrooscriptGrails(request, asset, out)

            String shortClassName = GrailsNameUtils.getShortName(fullClassName)
            String nameComponent = attrs.name ?: GrailsNameUtils.getScriptName(shortClassName)
            String convertedComponent =
                    javascriptConverter.getComponentCodeConverted(fullClassName, shortClassName, nameComponent)

            grooscriptGrailsHelpers.addAssetScript(asset, out, convertedComponent)
        }
    }

    private void initWebsocket() {
        def websocketStarted = request.getAttribute(WEBSOCKET_STARTED)
        if (!websocketStarted) {
            initSpringWebsocket([:], { -> ''})
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
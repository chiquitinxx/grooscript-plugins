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

import asset.pipeline.grails.AssetsTagLib
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.util.GrailsUtil
import grails.web.mapping.LinkGenerator
import org.grooscript.grails.Templates
import org.grooscript.grails.bean.JavascriptConverter
import org.grooscript.grails.event.GrooscriptEventsInterceptor
import org.grooscript.grails.util.GrooscriptGrailsHelpers
import org.grooscript.grails.util.JavascriptTemplate
import org.grooscript.grails.util.Util
import spock.lang.Specification
import spock.lang.Unroll

@TestMixin(GrailsUnitTestMixin)
@TestFor(GrooscriptTagLib)
class GrooscriptTagLibSpec extends Specification {

    private JavascriptConverter grooscriptConverter
    private AssetsTagLib assetsTagLib
    private JavascriptTemplate template = new JavascriptTemplate()
    private LinkGenerator linkGenerator = Mock(LinkGenerator)
    private GrooscriptGrailsHelpers grailsHelpers = Mock(GrooscriptGrailsHelpers)
    private GrooscriptEventsInterceptor grooscriptEventsInterceptor = Mock(GrooscriptEventsInterceptor)

    void setup() {
        grooscriptConverter = Mock(JavascriptConverter)
        assetsTagLib = Mock(AssetsTagLib)
        tagLib.javascriptConverter = grooscriptConverter
        tagLib.metaClass.asset = assetsTagLib
        tagLib.javascriptTemplate = template
        tagLib.grailsLinkGenerator = linkGenerator
        tagLib.grooscriptGrailsHelpers = grailsHelpers
        tagLib.grooscriptEventsInterceptor = grooscriptEventsInterceptor
    }

    static final String GROOVY_CODE = 'code example'
    static final String JS_CODE = 'js converted code'
    static final String TEMPLATE_NAME = 'template name'
    static final String wsPrefix = '/aPrefix'

    void 'test code taglib'() {
        when:
        applyTemplate("<grooscript:code>${GROOVY_CODE}</grooscript:code>")

        then:
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * grooscriptConverter.toJavascript(GROOVY_CODE, null) >> JS_CODE
        1 * grailsHelpers.addAssetScript(assetsTagLib, tagLib.out, JS_CODE)
        0 * _
    }

    void 'test code taglib with conversion options'() {
        when:
        applyTemplate("<grooscript:code conversionOptions='[recursive: true]'>${GROOVY_CODE}</grooscript:code>")

        then:
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * grooscriptConverter.toJavascript(GROOVY_CODE, [recursive: true]) >> JS_CODE
        1 * grailsHelpers.addAssetScript(assetsTagLib, tagLib.out, JS_CODE)
        0 * _
    }

    @Unroll
    void 'test basic template'() {
        given:
        GroovySpy(Util, global: true)

        when:
        def result = applyTemplate("<grooscript:template${extraCode}>assert true</grooscript:template>")

        then:
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * Util.newTemplateName >> TEMPLATE_NAME
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.TEMPLATE_DRAW, [
                    functionName: TEMPLATE_NAME, jsCode: JS_CODE, selector: "#$TEMPLATE_NAME"]) +
                        template.apply(Templates.TEMPLATE_ON_READY, [functionName: TEMPLATE_NAME])
        })
        1 * grooscriptConverter.toJavascript('def gsTextHtml = { data -> HtmlBuilder.build { builderIt -> assert true}}') >> JS_CODE
        0 * _
        result == '\n<div id=\'' + TEMPLATE_NAME + '\'></div>\n'

        where:
        extraCode << ['', ' onLoad="true"']
    }

    void 'very basic test template options'() {
        when:
        def result = applyTemplate("<grooscript:template functionName='jarJar'" +
                " itemSelector='#anyId' onLoad=\"${false}\">assert true</grooscript:template>")

        then:
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.TEMPLATE_DRAW, [
                    functionName: 'jarJar', jsCode: JS_CODE, selector: '#anyId'])
        })
        1 * grooscriptConverter.toJavascript('def gsTextHtml = { data -> HtmlBuilder.build { builderIt -> assert true}}') >> JS_CODE
        !result
    }

    void 'test remote model with domain class that exists'() {
        given:
        String domainClassName = 'package.AnyClass'

        when:
        applyTemplate("<grooscript:remoteModel domainClass='${domainClassName}'/>")

        then:
        1 * grailsHelpers.validDomainClassName(domainClassName) >> true
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * grooscriptConverter.convertRemoteDomainClass(domainClassName) >> JS_CODE
        1 * grailsHelpers.addAssetScript(assetsTagLib, tagLib.out, JS_CODE)
        0 * _
    }

    void 'test remote model with invalid domain class name'() {
        given:
        GroovySpy(Util, global: true)
        String domainClassName = 'package.AnyClass'

        when:
        applyTemplate("<grooscript:remoteModel domainClass='${domainClassName}'/>")

        then:
        1 * grailsHelpers.validDomainClassName(domainClassName) >> false
        1 * Util.consoleError(_)
    }

    void 'init spring websockets'() {
        when:
        applyTemplate('<grooscript:initSpringWebsocket />')

        then:
        interaction {
            initWebsockets()
        }
        0 * _
    }

    void 'init spring websockets with debug'() {
        when:
        applyTemplate('<grooscript:initSpringWebsocket withDebug="true"/>')

        then:
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET,
                    [endPoint: '/stomp', jsCode: '', withDebug: true, wsPrefix: wsPrefix])
        })
        1 * grailsHelpers.getWebsocketDestinationPrefix() >> wsPrefix
        0 * _
    }

    void 'init spring websockets without debug'() {
        when:
        applyTemplate('<grooscript:initSpringWebsocket withDebug="false"/>')

        then:
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET,
                    [endPoint: '/stomp', jsCode: '', withDebug: false, wsPrefix: wsPrefix])
        })
        1 * grailsHelpers.getWebsocketDestinationPrefix() >> wsPrefix
        0 * _
    }

    void 'init spring websockets with endPoint'() {
        when:
        applyTemplate('<grooscript:initSpringWebsocket endPoint="/hello"/>')

        then:
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET,
                    [endPoint: '/hello', jsCode: '', withDebug: false, wsPrefix: wsPrefix])
        })
        1 * grailsHelpers.getWebsocketDestinationPrefix() >> wsPrefix
        0 * _
    }

    void 'init spring websockets with connect function'() {
        when:
        applyTemplate('<grooscript:initSpringWebsocket>assert true</grooscript:initSpringWebsocket>')

        then:
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * grooscriptConverter.toJavascript('assert true') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET,
                    [endPoint: '/stomp', jsCode: JS_CODE, withDebug: false, wsPrefix: wsPrefix])
        })
        1 * grailsHelpers.getWebsocketDestinationPrefix() >> wsPrefix
        0 * _
    }

    void 'on grails event'() {
        when:
        applyTemplate('<grooscript:onGrailsEvent name="myEvent">assert true</grooscript:onGrailsEvent>')

        then:
        interaction {
            initWebsockets()
        }
        1 * grooscriptConverter.toJavascript('def run = { data -> assert true }') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.ON_SERVER_EVENT,
                    [jsCode: JS_CODE, path: '/topic/myEvent', functionName: 'gSonServerEvent0', type: 'null'])
        })
        1 * grooscriptEventsInterceptor.addEventToIntercept('myEvent')
        1 * grailsHelpers.getGrooscriptWebsocketTopicPrefix() >> '/topic/'
        0 * _
    }

    void 'on grails event with type response'() {
        when:
        applyTemplate('<grooscript:onGrailsEvent name="myEvent" type="Type">assert true</grooscript:onGrailsEvent>')

        then:
        interaction {
            initWebsockets()
        }
        1 * grooscriptConverter.toJavascript('def run = { data -> assert true }') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.ON_SERVER_EVENT,
                    [jsCode: JS_CODE, path: '/topic/myEvent', functionName: 'gSonServerEvent0', type: 'Type'])
        })
        1 * grooscriptEventsInterceptor.addEventToIntercept('myEvent')
        1 * grailsHelpers.getGrooscriptWebsocketTopicPrefix() >> '/topic/'
        0 * _
    }

    void 'on websocket message'() {
        when:
        applyTemplate('<grooscript:onWebsocket path="/path">assert true</grooscript:onWebsocket>')

        then:
        interaction {
            initWebsockets()
        }
        1 * grooscriptConverter.toJavascript('def run = { data -> assert true }') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.ON_SERVER_EVENT,
                    [jsCode: JS_CODE, path: '/path', functionName: 'gSonServerEvent0', type: 'null'])
        })
        0 * _
    }

    void 'on websocket message with type response'() {
        when:
        applyTemplate('<grooscript:onWebsocket path="/path2" type="Type">assert true</grooscript:onWebsocket>')

        then:
        interaction {
            initWebsockets()
        }
        1 * grooscriptConverter.toJavascript('def run = { data -> assert true }') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.ON_SERVER_EVENT,
                    [jsCode: JS_CODE, path: '/path2', functionName: 'gSonServerEvent0', type: 'Type'])
        })
        0 * _
    }

    @Unroll
    void 'add a component'() {
        when:
        applyTemplate("<grooscript:component src='${className}'/>")

        then:
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * grooscriptConverter.getComponentCodeConverted(className, componentClass, componentName) >> JS_CODE
        1 * grailsHelpers.addAssetScript(assetsTagLib, tagLib.out, JS_CODE)
        0 * _

        where:
        className                    | componentClass | componentName
        'org.grooscript.MyComponent' | 'MyComponent'  | 'my-component'
        'MyComponent'                | 'MyComponent'  | 'my-component'
        'Name'                       | 'Name'         | 'name'
    }

    void 'can define name of the component'() {
        given:
        String className = 'Counter'
        String componentName = 'my-counter'

        when:
        applyTemplate("<grooscript:component src='${className}' name='${componentName}'/>")

        then:
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * grooscriptConverter.getComponentCodeConverted(className, className, componentName) >> JS_CODE
        1 * grailsHelpers.addAssetScript(assetsTagLib, tagLib.out, JS_CODE)
        0 * _
    }

    void 'reload page when a message comes in a websocket path'() {
        given:
        GroovySpy(GrailsUtil, global: true)
        GroovySpy(Util, global: true)

        when:
        applyTemplate("<grooscript:reloadOn path='/myPath'/>")

        then:
        interaction {
            initWebsockets()
        }
        1 * GrailsUtil.isDevelopmentEnv() >> true
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.RELOAD_ON,
                    [path: '/myPath', functionName: 'gSonServerEvent0'])
        })
        0 * _
    }

    void 'reloadOn only works in development'() {
        given:
        GroovySpy(GrailsUtil, global: true)
        GroovySpy(Util, global: true)

        when:
        applyTemplate("<grooscript:reloadOn path='/myPath'/>")

        then:
        1 * GrailsUtil.isDevelopmentEnv() >> false
        0 * _
    }

    void 'reload page not init websockets if already started'() {
        given:
        GroovySpy(GrailsUtil, global: true)
        GroovySpy(Util, global: true)
        applyTemplate('<grooscript:initSpringWebsocket />')

        when:
        applyTemplate("<grooscript:reloadOn path='/myPath'/>")

        then:
        1 * GrailsUtil.isDevelopmentEnv() >> true
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.RELOAD_ON,
                    [path: '/myPath', functionName: 'gSonServerEvent0'])
        })
        0 * _
    }

    void 'path is mandatory in reloadOn'() {
        given:
        GroovySpy(GrailsUtil, global: true)
        GroovySpy(Util, global: true)

        when:
        applyTemplate("<grooscript:reloadOn/>")

        then:
        1 * GrailsUtil.isDevelopmentEnv() >> true
        1 * Util.consoleError('GrooscriptTagLib reloadOn need define path property')
    }

    private void initWebsockets() {
        1 * grailsHelpers.initGrooscriptGrails(request, assetsTagLib, tagLib.out)
        1 * grailsHelpers.getWebsocketDestinationPrefix() >> wsPrefix
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET,
                    [endPoint: '/stomp', jsCode: '', withDebug: false, wsPrefix: wsPrefix])
        })
    }
}

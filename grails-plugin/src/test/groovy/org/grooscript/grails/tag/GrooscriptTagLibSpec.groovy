package org.grooscript.grails.tag

import asset.pipeline.grails.AssetsTagLib
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.util.GrailsUtil
import grails.web.mapping.LinkGenerator
import org.grooscript.grails.Templates
import org.grooscript.grails.bean.GrooscriptConverter
import org.grooscript.grails.util.GrooscriptTemplate
import org.grooscript.grails.util.Util
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Jorge Franco
 * Date: 08/06/14
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(GrooscriptTagLib)
class GrooscriptTagLibSpec extends Specification {

    private GrooscriptConverter grooscriptConverter
    private AssetsTagLib assetsTagLib
    private GrooscriptTemplate template = new GrooscriptTemplate()
    private LinkGenerator linkGenerator = Mock(LinkGenerator)
    private static final String CANONICAL_NAME = 'canonicalName'
    private stubDomainClass = [
            name: DOMAIN_CLASS_NAME,
            fullName: DOMAIN_CLASS_NAME_WITH_PACKAGE,
            clazz: [canonicalName: CANONICAL_NAME]
    ]

    static final FAKE_NAME = 'FAKE'
    static final DOMAIN_CLASS_NAME = 'Domain'
    static final DOMAIN_CLASS_NAME_WITH_PACKAGE = 'package.Domain'

    void setup() {
        grooscriptConverter = Mock(GrooscriptConverter)
        assetsTagLib = Mock(AssetsTagLib)
        tagLib.grooscriptConverter = grooscriptConverter
        tagLib.metaClass.asset = assetsTagLib
        tagLib.grooscriptTemplate = template
        tagLib.grailsLinkGenerator = linkGenerator
    }

    void cleanup() {
    }

    static final GROOVY_CODE = 'code example'
    static final JS_CODE = 'js converted code'
    static final REMOTE_URL = 'my url'
    static final TEMPLATE_NAME = 'template name'
    static final COMPONENT_GROOVY_CODE = 'class Component { def draw() {} }'
    static final COMPONENT_JS_CODE = 'component code in javascript'

    void 'test code taglib'() {
        when:
        applyTemplate("<grooscript:code>${GROOVY_CODE}</grooscript:code>")

        then:
        interaction {
            initGrooscriptGrails()
        }
        1 * grooscriptConverter.toJavascript(GROOVY_CODE, null) >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], { it() == JS_CODE})
        0 * _
    }

    void 'test code taglib with conversion options'() {
        when:
        applyTemplate("<grooscript:code conversionOptions='[recursive: true]'>${GROOVY_CODE}</grooscript:code>")

        then:
        interaction {
            initGrooscriptGrails()
        }
        1 * grooscriptConverter.toJavascript(GROOVY_CODE, [recursive: true]) >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], { it() == JS_CODE})
        0 * _
    }

    @Unroll
    void 'test basic template'() {
        given:
        GroovySpy(Util, global: true)

        when:
        def result = applyTemplate("<grooscript:template${extraCode}>assert true</grooscript:template>")

        then:
        interaction {
            initGrooscriptGrails()
        }
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

    void 'test template with onEvent option'() {
        given:
        GroovySpy(Util, global: true)

        when:
        applyTemplate("<grooscript:template onEvent='myEvent' onLoad=\"${false}\">assert true</grooscript:template>")

        then:
        1 * Util.newTemplateName >> TEMPLATE_NAME
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.CLIENT_EVENT, [
                    functionName: TEMPLATE_NAME, nameEvent: 'myEvent'])
        })
        1 * grooscriptConverter.toJavascript(_) >> JS_CODE
    }

    void 'test template with onEvent list option'() {
        given:
        GroovySpy(Util, global: true)

        when:
        applyTemplate("<grooscript:template onEvent='eventOne, eventTwo' onLoad=\"${false}\">assert true</grooscript:template>")

        then:
        1 * Util.newTemplateName >> TEMPLATE_NAME
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.CLIENT_EVENT, [
                    functionName: TEMPLATE_NAME, nameEvent: 'eventOne'])
        })
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.CLIENT_EVENT, [
                    functionName: TEMPLATE_NAME, nameEvent: 'eventTwo'])
        })
        1 * grooscriptConverter.toJavascript(_) >> JS_CODE
    }

    void 'test onEvent tag'() {
        given:
        GroovySpy(Util, global: true)

        when:
        def result = applyTemplate("<grooscript:onEvent name='myEvent'>assert true</grooscript:onEvent>")

        then:
        interaction {
            initGrooscriptGrails()
        }
        1 * Util.removeLastSemicolon(JS_CODE)
        1 * grooscriptConverter.toJavascript('{ event -> assert true}') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.ON_EVENT_TAG, [nameEvent: 'myEvent', jsCode: JS_CODE])
        })
        0 * _
        result == ''
    }

    @Unroll
    void 'test remote model with domain class in development'() {
        given:
        GroovySpy(GrailsUtil, global: true)

        when:
        stubGrailsApplication()
        applyTemplate("<grooscript:remoteModel domainClass='${domainClassName}'/>")

        then:
        numberTimes * GrailsUtil.isDevelopmentEnv() >> true
        numberTimes * assetsTagLib.script(['type':'text/javascript'], {
            it() == JS_CODE
        })
        numberTimes * grooscriptConverter.convertRemoteDomainClass(CANONICAL_NAME) >> JS_CODE

        where:
        domainClassName                | numberTimes
        FAKE_NAME                      | 0
        DOMAIN_CLASS_NAME              | 1
        DOMAIN_CLASS_NAME_WITH_PACKAGE | 1
    }

    @Unroll
    void 'test remote model with domain class in production'() {
        given:
        GroovySpy(GrailsUtil, global: true)
        GroovySpy(Util, global: true)

        when:
        stubGrailsApplication()
        applyTemplate("<grooscript:remoteModel domainClass='${domainClassName}'/>")

        then:
        1 * GrailsUtil.isDevelopmentEnv() >> false
        1 * Util.getResourceText('Domain.gs') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == JS_CODE
        })

        where:
        domainClassName << [DOMAIN_CLASS_NAME, DOMAIN_CLASS_NAME_WITH_PACKAGE]
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
        interaction {
            initGrooscriptGrails()
        }
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET, [endPoint: '/stomp', jsCode: '', withDebug: true])
        })
        0 * _
    }

    void 'init spring websockets without debug'() {
        when:
        applyTemplate('<grooscript:initSpringWebsocket withDebug="false"/>')

        then:
        interaction {
            initGrooscriptGrails()
        }
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET, [endPoint: '/stomp', jsCode: '', withDebug: false])
        })
        0 * _
    }

    void 'init spring websockets with endPoint'() {
        when:
        applyTemplate('<grooscript:initSpringWebsocket endPoint="/hello"/>')

        then:
        interaction {
            initGrooscriptGrails()
        }
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET, [endPoint: '/hello', jsCode: '', withDebug: false])
        })
        0 * _
    }

    void 'init spring websockets with connect function'() {
        when:
        applyTemplate('<grooscript:initSpringWebsocket>assert true</grooscript:initSpringWebsocket>')

        then:
        interaction {
            initGrooscriptGrails()
        }
        1 * grooscriptConverter.toJavascript('assert true') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET, [endPoint: '/stomp', jsCode: JS_CODE, withDebug: false])
        })
        0 * _
    }

    void 'on server event'() {
        when:
        applyTemplate('<grooscript:onServerEvent path="/myPath">assert true</grooscript:onServerEvent>')

        then:
        interaction {
            initWebsockets()
        }
        1 * grooscriptConverter.toJavascript('def run = { data -> assert true }') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.ON_SERVER_EVENT,
                    [jsCode: JS_CODE, path: '/myPath', functionName: 'gSonServerEvent0', type: 'null'])
        })
        0 * _
    }

    void 'on server event with type response'() {
        when:
        applyTemplate('<grooscript:onServerEvent path="/myPath" type="Type">assert true</grooscript:onServerEvent>')

        then:
        interaction {
            initWebsockets()
        }
        1 * grooscriptConverter.toJavascript('def run = { data -> assert true }') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.ON_SERVER_EVENT,
                    [jsCode: JS_CODE, path: '/myPath', functionName: 'gSonServerEvent0', type: 'Type'])
        })
        0 * _
    }

    @Unroll
    void 'add a component in development'() {
        given:
        GroovySpy(GrailsUtil, global: true)
        GroovySpy(Util, global: true)

        when:
        applyTemplate("<grooscript:component src='${className}'/>")

        then:
        interaction {
            initGrooscriptGrails()
        }
        1 * GrailsUtil.isDevelopmentEnv() >> true
        1 * Util.getClassSource(className) >> COMPONENT_GROOVY_CODE
        1 * grooscriptConverter.convertComponent(COMPONENT_GROOVY_CODE) >> COMPONENT_JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == "${COMPONENT_JS_CODE};GrooscriptGrails.createComponent(${componentClass}, '${componentName}');"
        })
        0 * _

        where:
        className                    | componentClass | componentName
        'org.grooscript.MyComponent' | 'MyComponent'  | 'my-component'
        'MyComponent'                | 'MyComponent'  | 'my-component'
        'Name'                       | 'Name'         | 'name'
    }

    @Unroll
    void 'add a component in production'() {
        given:
        GroovySpy(GrailsUtil, global: true)
        GroovySpy(Util, global: true)

        when:
        applyTemplate("<grooscript:component src='${className}'/>")

        then:
        interaction {
            initGrooscriptGrails()
        }
        1 * GrailsUtil.isDevelopmentEnv() >> false
        1 * Util.getResourceText("${componentClass}.cs") >> COMPONENT_JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == "${COMPONENT_JS_CODE};GrooscriptGrails.createComponent(${componentClass}, '${componentName}');"
        })
        0 * _

        where:
        className                    | componentClass | componentName
        'org.grooscript.MyComponent' | 'MyComponent'  | 'my-component'
        'MyComponent'                | 'MyComponent'  | 'my-component'
        'Name'                       | 'Name'         | 'name'
    }

    void 'can define name of the component'() {
        given:
        GroovySpy(GrailsUtil, global: true)
        GroovySpy(Util, global: true)

        when:
        applyTemplate("<grooscript:component src='Counter' name='my-counter'/>")

        then:
        interaction {
            initGrooscriptGrails()
        }
        1 * GrailsUtil.isDevelopmentEnv() >> true
        1 * Util.getClassSource('Counter') >> COMPONENT_GROOVY_CODE
        1 * grooscriptConverter.convertComponent(COMPONENT_GROOVY_CODE) >> COMPONENT_JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == "${COMPONENT_JS_CODE};GrooscriptGrails.createComponent(Counter, 'my-counter');"
        })
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

    private stubGrailsApplication() {
        tagLib.grailsApplication.metaClass.getDomainClasses = { -> [stubDomainClass] }
    }

    private void initGrooscriptGrails() {
        1 * linkGenerator.getServerBaseURL() >> REMOTE_URL
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.INIT_GROOSCRIPT_GRAILS, [remoteUrl: REMOTE_URL])
        })
    }

    private void initWebsockets() {
        initGrooscriptGrails()
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET, [endPoint: '/stomp', jsCode: '', withDebug: false])
        })
    }
}

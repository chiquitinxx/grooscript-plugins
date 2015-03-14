package org.grooscript.grails.tag

import asset.pipeline.grails.AssetsTagLib
import grails.core.GrailsApplication
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
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

    void 'test code taglib'() {
        when:
        applyTemplate("<grooscript:code>${GROOVY_CODE}</grooscript:code>")

        then:
        1 * grooscriptConverter.toJavascript(GROOVY_CODE, null) >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], { it() == JS_CODE})
        0 * _
    }

    void 'test code taglib with conversion options'() {
        when:
        applyTemplate("<grooscript:code conversionOptions='[recursive: true]'>${GROOVY_CODE}</grooscript:code>")

        then:
        1 * grooscriptConverter.toJavascript(GROOVY_CODE, [recursive: true]) >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], { it() == JS_CODE})
        0 * _
    }

    void 'test basic template'() {
        given:
        GroovySpy(Util, global: true)

        when:
        def result = applyTemplate("<grooscript:template>assert true</grooscript:template>")

        then:
        1 * Util.newTemplateName >> TEMPLATE_NAME
        1 * linkGenerator.getServerBaseURL() >> REMOTE_URL
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.INIT_GROOSCRIPT_GRAILS, [remoteUrl: REMOTE_URL])
        })
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.TEMPLATE_DRAW, [
                    functionName: TEMPLATE_NAME, jsCode: JS_CODE, selector: "#$TEMPLATE_NAME"]) +
                        template.apply(Templates.TEMPLATE_ON_READY, [functionName: TEMPLATE_NAME])
        })
        1 * grooscriptConverter.toJavascript('def gsTextHtml = { data -> HtmlBuilder.build { -> assert true}}') >> JS_CODE
        0 * _
        result == "\n<div id='$TEMPLATE_NAME'></div>\n"
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
        1 * grooscriptConverter.toJavascript('def gsTextHtml = { data -> HtmlBuilder.build { -> assert true}}') >> JS_CODE
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
        1 * grooscriptConverter.toJavascript('{ event -> assert true}') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.ON_EVENT_TAG, [nameEvent: 'myEvent', jsCode: JS_CODE])
        })
        result == ''
    }

    @Unroll
    void 'test model with domain class'() {
        when:
        stubGrailsApplication()
        applyTemplate("<grooscript:model domainClass='${domainClassName}'/>")

        then:
        numberTimes * assetsTagLib.script(['type':'text/javascript'], {
            it() == JS_CODE
        })
        numberTimes * grooscriptConverter.convertDomainClass(CANONICAL_NAME) >> JS_CODE

        where:
        domainClassName                | numberTimes
        FAKE_NAME                      | 0
        DOMAIN_CLASS_NAME              | 1
        DOMAIN_CLASS_NAME_WITH_PACKAGE | 1
    }

    @Unroll
    void 'test remote model with domain class'() {
        when:
        stubGrailsApplication()
        applyTemplate("<grooscript:remoteModel domainClass='${domainClassName}'/>")

        then:
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

    void 'init websockets spring plugin'() {
        when:
        applyTemplate('<grooscript:initSpringWebsocket />')

        then:
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET, [url: '/stomp', jsCode: ''])
        })
        0 * _
    }

    void 'init websockets spring plugin with connect function'() {
        when:
        applyTemplate('<grooscript:initSpringWebsocket>assert true</grooscript:initSpringWebsocket>')

        then:
        1 * grooscriptConverter.toJavascript('assert true') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.SPRING_WEBSOCKET, [url: '/stomp', jsCode: JS_CODE])
        })
        0 * _
    }

    void 'on server event'() {
        when:
        applyTemplate('<grooscript:onServerEvent path="/myPath">assert true</grooscript:onServerEvent>')

        then:
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
        1 * grooscriptConverter.toJavascript('def run = { data -> assert true }') >> JS_CODE
        1 * assetsTagLib.script(['type':'text/javascript'], {
            it() == template.apply(Templates.ON_SERVER_EVENT,
                    [jsCode: JS_CODE, path: '/myPath', functionName: 'gSonServerEvent0', type: 'Type'])
        })
        0 * _
    }

    private stubGrailsApplication() {
        tagLib.grailsApplication.metaClass.getDomainClasses = { -> [stubDomainClass] }
    }
}

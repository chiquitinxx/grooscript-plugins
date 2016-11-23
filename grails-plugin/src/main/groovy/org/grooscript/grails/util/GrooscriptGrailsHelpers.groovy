package org.grooscript.grails.util

import grails.core.GrailsApplication
import grails.web.mapping.LinkGenerator
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

class GrooscriptGrailsHelpers {

    private static final String WEBSOCKET_BEAN = 'brokerMessagingTemplate'
    private static final String GROOSCRIPT_JS_LIB_ADDED = 'grooscriptJsLibAdded'
    private static final String GROOSCRIPT_JS_LIB = 'grooscript-grails.js'
    private static final String WEBSOKET_HANDLER = 'grailsSimpAnnotationMethodMessageHandler'
    private static final String DEFAULT_WEBSOCKET_DESTINATION_PREFIX = '/app'
    private static final String DEFAULT_WEBSOCKET_TOPIC_PEFIX = '/topic'

    private LinkGenerator grailsLinkGenerator
    private JavascriptTemplate grooscriptTemplate
    private GrailsApplication grailsApplication
    private ApplicationContext applicationContext

    @Autowired
    GrooscriptGrailsHelpers(LinkGenerator grailsLinkGenerator,
                                   JavascriptTemplate grooscriptTemplate,
                                   GrailsApplication grailsApplication,
                                   ApplicationContext applicationContext) {
        this.grailsLinkGenerator = grailsLinkGenerator
        this.grooscriptTemplate = grooscriptTemplate
        this.grailsApplication = grailsApplication
        this.applicationContext = applicationContext
    }

    void addAssetScript(assetTagLib, out, String content) {
        out << assetTagLib.script([type: 'text/javascript'], content)
    }

    void initGrooscriptGrails(request, assetTagLib, out) {
        def gsLibAdded = request.getAttribute(GROOSCRIPT_JS_LIB_ADDED)
        if (!gsLibAdded) {
            out << assetTagLib.javascript(src: GROOSCRIPT_JS_LIB)
            request.setAttribute(GROOSCRIPT_JS_LIB_ADDED, true)
        }
    }

    boolean validDomainClassName(String name) {
        if (!name || !(name instanceof String)) {
            Util.consoleError "GrooscriptTagLib have to define domainClass property as String"
        } else {
            if (domainClassFromName(name)) {
                return true
            } else {
                Util.consoleError "Not exist domain class ${name}"
            }
        }
        return false
    }

    static boolean isSpringWebsocketsActive(ApplicationContext applicationContext) {
        try {
            applicationContext.getBean(WEBSOCKET_BEAN)
            true
        } catch (NoSuchBeanDefinitionException e) {
            return false
        }
    }

    void sendWebsocketEventMessage(String key, Object data) {
        if (isSpringWebsocketsActive(applicationContext)) {
            try {
                applicationContext.getBean(WEBSOCKET_BEAN).convertAndSend key, data
            } catch (Exception e) {
                Util.consoleError("Fail sending websocket message of type " + data.class)
                Util.consoleError("Error: " + e.message)
            }
        }
    }

    String getWebsocketDestinationPrefix() {
        try {
            def bean = applicationContext.getBean(WEBSOKET_HANDLER)
            return removeLastSlash(bean?.destinationPrefixes?.first() ?: DEFAULT_WEBSOCKET_DESTINATION_PREFIX)
        } catch (NoSuchBeanDefinitionException e) {
            return DEFAULT_WEBSOCKET_DESTINATION_PREFIX
        }
    }

    String getGrooscriptWebsocketTopicPrefix() {
        DEFAULT_WEBSOCKET_TOPIC_PEFIX + '/gswsevent/'
    }

    private boolean domainClassFromName(String nameClass) {
        grailsApplication.getDomainClasses().find { it.fullName == nameClass }
    }

    private String removeLastSlash(String path) {
        if (path.endsWith("/")) {
            path.substring(0, path.length() - 1)
        } else {
            path
        }
    }
}

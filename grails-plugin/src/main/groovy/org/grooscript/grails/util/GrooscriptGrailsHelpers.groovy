package org.grooscript.grails.util

import grails.core.GrailsApplication
import grails.web.mapping.LinkGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

class GrooscriptGrailsHelpers {

    private static final String REMOTE_URL_SET = 'grooscriptRemoteUrl'

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
        def urlSet = request.getAttribute(REMOTE_URL_SET)
        if (!urlSet) {
            request.setAttribute(REMOTE_URL_SET, true)
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

    boolean isSpringWebsocketsActive() {
        applicationContext.getBean('brokerMessagingTemplate')
    }

    void sendWebsocketMessake(String key, Object data) {
        if (springWebsocketsActive) {
            applicationContext.getBean('brokerMessagingTemplate').convertAndSend key, data
        }
    }

    private boolean domainClassFromName(String nameClass) {
        grailsApplication.getDomainClasses().find { it.fullName == nameClass }
    }
}

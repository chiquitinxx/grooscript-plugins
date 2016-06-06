package org.grooscript.grails.util

import grails.core.GrailsApplication
import grails.web.mapping.LinkGenerator
import org.grails.spring.GrailsApplicationContext
import org.grooscript.grails.Templates
import org.springframework.beans.factory.annotation.Autowired

class GrooscriptGrailsHelpers {

    private static final String REMOTE_URL_SET = 'grooscriptRemoteUrl'

    private LinkGenerator grailsLinkGenerator
    private JavascriptTemplate grooscriptTemplate
    private GrailsApplication grailsApplication
    private GrailsApplicationContext grailsApplicationContext

    @Autowired
    GrooscriptGrailsHelpers(LinkGenerator grailsLinkGenerator,
                                   JavascriptTemplate grooscriptTemplate,
                                   GrailsApplication grailsApplication,
                                   GrailsApplicationContext grailsApplicationContext) {
        this.grailsLinkGenerator = grailsLinkGenerator
        this.grooscriptTemplate = grooscriptTemplate
        this.grailsApplication = grailsApplication
        this.grailsApplicationContext = grailsApplicationContext
    }

    void addAssetScript(assetTagLib, out, String content) {
        out << assetTagLib.script([type: 'text/javascript'], content)
    }

    void initGrooscriptGrails(request, assetTagLib, out) {
        def urlSet = request.getAttribute(REMOTE_URL_SET)
        if (!urlSet) {
            String content = grooscriptTemplate.apply(
                    Templates.INIT_GROOSCRIPT_GRAILS,
                    [remoteUrl: grailsLinkGenerator.serverBaseURL])
            addAssetScript(assetTagLib, out, content)
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
        grailsApplicationContext.getBean('brokerMessagingTemplate')
    }

    void sendWebsocketMessake(String key, Object data) {
        if (springWebsocketsActive) {
            grailsApplicationContext.brokerMessagingTemplate.convertAndSend key, data
        }
    }

    private boolean domainClassFromName(String nameClass) {
        grailsApplication.getDomainClasses().find { it.fullName == nameClass }
    }
}

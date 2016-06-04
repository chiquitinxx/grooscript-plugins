package org.grooscript.grails.util;

import asset.pipeline.grails.AssetsTagLib
import grails.core.GrailsApplication
import grails.web.mapping.LinkGenerator
import org.grails.buffer.GrailsPrintWriter
import org.grooscript.grails.Templates
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest;

@Component
public class GrailsHelpers {

    private static final String REMOTE_URL_SET = 'grooscriptRemoteUrl'

    private LinkGenerator grailsLinkGenerator
    private JavascriptTemplate grooscriptTemplate
    private GrailsApplication grailsApplication

    public GrailsHelpers(LinkGenerator grailsLinkGenerator, JavascriptTemplate grooscriptTemplate,
                         GrailsApplication grailsApplication) {
        this.grailsLinkGenerator = grailsLinkGenerator
        this.grooscriptTemplate = grooscriptTemplate
        this.grailsApplication = grailsApplication
    }

    public void addAssetScript(AssetsTagLib asset, GrailsPrintWriter out, String content) {
        out << asset.script([type: 'text/javascript'], content)
    }

    public void initGrooscriptGrails(HttpServletRequest request, AssetsTagLib asset, GrailsPrintWriter out) {
        def urlSet = request.getAttribute(REMOTE_URL_SET)
        if (!urlSet) {
            String content = grooscriptTemplate.apply(
                    Templates.INIT_GROOSCRIPT_GRAILS,
                    [remoteUrl: grailsLinkGenerator.serverBaseURL])
            addAssetScript(asset, out, content)
            request.setAttribute(REMOTE_URL_SET, true)
        }
    }

    public boolean validDomainClassName(String name) {
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

    private boolean domainClassFromName(String nameClass) {
        grailsApplication.getDomainClasses().find { it.fullName == nameClass }
    }
}

package org.grooscript.grails.util;

import asset.pipeline.grails.AssetsTagLib
import grails.web.mapping.LinkGenerator
import org.grails.buffer.GrailsPrintWriter
import org.grooscript.grails.Templates
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest;

@Component
public class GrailsHelpers {

    private static final String REMOTE_URL_SET = 'grooscriptRemoteUrl'

    private LinkGenerator grailsLinkGenerator
    private GrooscriptTemplate grooscriptTemplate

    public GrailsHelpers(LinkGenerator grailsLinkGenerator, GrooscriptTemplate grooscriptTemplate) {
        this.grailsLinkGenerator = grailsLinkGenerator
        this.grooscriptTemplate = grooscriptTemplate
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
}

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
package org.grooscript.grails.util

import grails.core.GrailsApplication
import grails.web.mapping.LinkGenerator
import org.springframework.context.ApplicationContext
import spock.lang.Specification

class GrooscriptGrailsHelpersSpec extends Specification {

    void 'get destination prefix from bean'() {
        given:
        applicationContext.getBean('grailsSimpAnnotationMethodMessageHandler') >> [
                destinationPrefixes: ['/app/']
        ]

        expect:
        grailsHelpers.websocketDestinationPrefix == '/app'
    }

    private ApplicationContext applicationContext = Stub(ApplicationContext)
    private GrooscriptGrailsHelpers grailsHelpers = new GrooscriptGrailsHelpers(Stub(LinkGenerator),
            Stub(JavascriptTemplate), Stub(GrailsApplication), applicationContext)
}

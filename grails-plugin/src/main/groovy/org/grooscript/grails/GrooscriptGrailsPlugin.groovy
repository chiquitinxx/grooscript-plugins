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
package org.grooscript.grails

import grails.plugins.Plugin

import org.grooscript.grails.bean.JavascriptConverter
import org.grooscript.grails.event.GrooscriptEventsInterceptor
import org.grooscript.grails.util.GrooscriptGrailsHelpers
import org.grooscript.grails.util.JavascriptTemplate
import org.grooscript.grails.util.Util

class GrooscriptGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/assets/javascripts/app/**",
        "grails-app/controllers/**",
        "grails-app/domain/**",
        "grails-app/views/**",
        "src/main/web-app/**",
    ]

    def title = "Grooscript" // Headline display name of the plugin
    def author = "Jorge Franco"
    def authorEmail = 'grooscript@gmail.com'
    def description = '''\
Use grooscript to work in the client side with your groovy code.
It converts the code to javascript and your groovy code will run in the browser.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grooscript.org/grails3-plugin/"

    def license = "APACHE"

    def developers = [ [ name: "Christian Meyer", email: "" ]]

    def issueManagement = [ system: "Github", url: "https://github.com/chiquitinxx/grooscript-grails3-plugin/issues" ]

    def scm = [ url: "https://github.com/chiquitinxx/grooscript-grails3-plugin" ]

    def loadAfter = ['springWebsocket']

    @Override
    Closure doWithSpring() {
        { ->
            javascriptConverter(JavascriptConverter)
            javascriptTemplate(JavascriptTemplate)
            grooscriptGrailsHelpers(GrooscriptGrailsHelpers)
            if (manager?.hasGrailsPlugin('springWebsocket')) {
                Util.consoleMessage 'Websocket support included'
                grooscriptEventsInterceptor(GrooscriptEventsInterceptor)
            }
        } 
    }
}

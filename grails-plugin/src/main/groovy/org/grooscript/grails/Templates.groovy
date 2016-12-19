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

class Templates {

    static final String TEMPLATE_DRAW = '''
function ${functionName}(templateParams) {
  ${jsCode}
  var code = gsTextHtml(templateParams);
  GsHlp.selectorHtml('${selector}', code);
};
'''

    static final String TEMPLATE_ON_READY = '''
GsHlp.onReady(function(event) {
  ${functionName}();
})
'''

    static final String SPRING_WEBSOCKET = '''
GsHlp.onReady(function(event) {

    var socket = new SockJS("${endPoint}");
    GrooscriptGrails.websocketClient = Stomp.over(socket);
    ${withDebug ? '' : 'GrooscriptGrails.websocketClient.debug = null;'}
    GrooscriptGrails.websocketClient.connect({}, function() {
        proccessWebsocketSubscribes();
        ${jsCode}
    });

    function proccessWebsocketSubscribes() {
        var i = 0;
        while (i >= 0) {
          try {
            var func = eval('gSonServerEvent' + (i++));
            if (func !== undefined && typeof func === "function") {
                func();
            } else {
                i = -1;
            }
          } catch (e) {
            i = -1;
          }
        }
    }
});
GrooscriptGrails.wsPrefix = "${wsPrefix}";
'''

    static final String ON_SERVER_EVENT_RUN = '''def run = { data -> ${code} }'''

    static final String ON_SERVER_EVENT = '''
var ${functionName} = function() {
    GrooscriptGrails.websocketClient.subscribe("${path}", function(message) {
        ${jsCode}
        var body;
        try {
            body = JSON.parse(message.body);
            run(gs.toGroovy(body, ${type}));
        } catch(e) {
            run(message.body);
        }
    });
};
'''

    static final String RELOAD_ON = '''
var ${functionName} = function() {
    GrooscriptGrails.websocketClient.subscribe("${path}", function(message) {
        window.location.reload();
    });
};
'''
}

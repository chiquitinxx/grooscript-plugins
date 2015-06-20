package org.grooscript.grails

/**
 * User: jorgefrancoleza
 * Date: 11/06/14
 */
class Templates {

    static final INIT_GROOSCRIPT_GRAILS = '''
  GrooscriptGrails.remoteUrl = '${remoteUrl}';
'''

    static final TEMPLATE_DRAW = '''
function ${functionName}(templateParams) {
  ${jsCode}
  var code = gsTextHtml(templateParams);
  \\$('${selector}').html(code);
};
'''

    static final TEMPLATE_ON_READY = '''
\\$(document).ready(function() {
  ${functionName}();
})
'''

    static final CLIENT_EVENT = '''
gsEvents.onEvent('${nameEvent}', ${functionName});
'''

    static final ON_EVENT_TAG = '''
gsEvents.onEvent('${nameEvent}', ${jsCode});
'''

    static final SPRING_WEBSOCKET = '''
\\$(document).ready(function() {

    var socket = new SockJS("${url}");
    websocketClient = Stomp.over(socket);
    ${withDebug ? '' : 'websocketClient.debug = null;'}
    websocketClient.connect({}, function() {
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
'''

    static final ON_SERVER_EVENT_RUN = '''def run = { data -> ${code} }'''

    static final ON_SERVER_EVENT = '''
var ${functionName} = function() {
    websocketClient.subscribe("${path}", function(message) {
        ${jsCode}
        run(gs.toGroovy(jQuery.parseJSON(message.body), ${type}));
    });
};
'''
}

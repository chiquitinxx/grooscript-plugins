package org.grooscript.grails

class Templates {

    static final String INIT_GROOSCRIPT_GRAILS = '''
  GrooscriptGrails.remoteUrl = '${remoteUrl}';
'''

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

    static final String CLIENT_EVENT = '''
gsEvents.onEvent('${nameEvent}', ${functionName});
'''

    static final String ON_EVENT_TAG = '''
gsEvents.onEvent('${nameEvent}', ${jsCode});
'''

    static final String SPRING_WEBSOCKET = '''
GsHlp.onReady(function(event) {

    var socket = new SockJS("${endPoint}");
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

    static final String ON_SERVER_EVENT_RUN = '''def run = { data -> ${code} }'''

    static final String ON_SERVER_EVENT = '''
var ${functionName} = function() {
    websocketClient.subscribe("${path}", function(message) {
        ${jsCode}
        run(gs.toGroovy(jQuery.parseJSON(message.body), ${type}));
    });
};
'''

    static final String RELOAD_ON = '''
var ${functionName} = function() {
    websocketClient.subscribe("${path}", function(message) {
        window.location.reload();
    });
};
'''
}

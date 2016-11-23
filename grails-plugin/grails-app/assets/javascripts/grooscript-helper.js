var GsHlp = {
    selectorHtml: function(selector, html) {
        var myNodeList = document.querySelectorAll(selector);
        for (var i = 0; i < myNodeList.length; ++i) {
            myNodeList[i].innerHTML = html;
        }
    },
    appendHtml: function(selector, html) {
        var myNodeList = document.querySelectorAll(selector);
        for (var i = 0; i < myNodeList.length; ++i) {
            myNodeList[i].innerHTML = myNodeList[i].innerHTML + html;
        }
    },
    onReady: function(func) {
        document.addEventListener("DOMContentLoaded", func);
    },
    serialize: function (obj) {
        var str = [];
        for(var p in obj)
            if (obj.hasOwnProperty(p)) {
                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
            }
        return str.join("&");
    },
    http: function(url, action, params, success, fail, responseClass) {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4 && (xhr.status > 199 && xhr.status < 300)) {
                if (success !== null && success !== undefined) {
                    var response;
                    try {
                        response = JSON.parse(this.responseText);
                        success(gs.toGroovy(response, responseClass));
                    } catch(e) {
                        success(this.responseText);
                    }
                }
            } else {
                if (fail !== null && fail !== undefined) {
                    fail(xhr.responseText);
                }
            }
        };
        var data = gs.isGroovyObj(params) ? gs.toJavascript(params) : params;
        var finalUrl = url;
        if (data) {
            finalUrl = finalUrl + '?' + GsHlp.serialize(data);
        }
        xhr.open(action || "GET", finalUrl, true);
        xhr.setRequestHeader('Accept','application/json; charset=utf-8');
        xhr.send(null);
    }
};

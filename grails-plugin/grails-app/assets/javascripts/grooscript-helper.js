var GsHlp = {
    selectorHtml: function(selector, html) {
        var myNodeList = document.querySelectorAll(selector);
        for (var i = 0; i < myNodeList.length; ++i) {
            myNodeList[i].innerHTML = html;
        }
    },
    onReady: function(func) {
        document.addEventListener("DOMContentLoaded", func);
    }
};

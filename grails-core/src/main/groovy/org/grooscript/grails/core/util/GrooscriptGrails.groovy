package org.grooscript.grails.core.util

import org.grooscript.asts.GsNative

class GrooscriptGrails {

    static String remoteUrl
    static String controllerRemoteDomain = 'remoteDomain'
    static String actionRemoteDomain = 'doAction'
    static long count = 0
    static Map components = [:]

    static final List GRAILS_PROPERTIES = ['url', 'class', 'clazz', 'gsName',
        'transients', 'constraints', 'mapping', 'hasMany', 'belongsTo', 'validationSkipMap',
        'gormPersistentEntity', 'properties', 'gormDynamicFinders', 'all', 'domainClass', 'attached',
        'validationErrorsMap', 'dirtyPropertyNames', 'errors', 'dirty', 'count']

    static register(component) {
        def number = count++
        component.cId = number
        components["id${number}"] = component
        component
    }

    static recover(cId) {
        components["id${cId}"]
    }

    @GsNative
    static Map getRemoteDomainClassProperties(remoteDomainClass) {/*
        var data;
        var result = gs.map();
        for (data in remoteDomainClass) {
            if ((typeof remoteDomainClass[data] !== "function") && !GrooscriptGrails.GRAILS_PROPERTIES.contains(data)) {
                result.add(data, remoteDomainClass[data]);
            }
        }
        return result;
    */}

    @GsNative
    static void doRemoteCall(String controller, String action, params, onSuccess, onFailure) {/*
        var url = GrooscriptGrails.remoteUrl;
        url = url + '/' + controller;
        if (action !== null) {
            url = url + '/' + action;
        }
        $.ajax({
            type: "POST",
            data: (gs.isGroovyObj(params) ? gs.toJavascript(params) : params),
            url: url
        }).done(function(newData) {
            if (onSuccess !== null && onSuccess !== undefined) {
                var successData = gs.toGroovy(newData);
                onSuccess(successData);
            }
        })
        .fail(function(error) {
            if (onFailure !== null && onFailure !== undefined) {
                onFailure(error);
            }
        });
    */}

    @GsNative
    static void remoteDomainAction(params, onSuccess, onFailure, name) {/*
        var url = GrooscriptGrails.remoteUrl + params.url;
        var data = (gs.isGroovyObj(params.data) ? gs.toJavascript(params.data) : params.data);
        var type = 'GET';
        if (params.action == 'create') {
            type = 'POST';
        }
        if (params.action == 'update') {
            type = 'PUT';
        }
        if (params.action == 'delete') {
            type = 'DELETE';
        }
        if (params.action == 'read' || params.action == 'delete') {
            data = null;
            url = url + '/' + params.data.id;
        }
        $.ajax({
            type: type,
            data: data,
            url: url,
            accepts: 'application/json'
        }).done(function(newData) {
            var successData = gs.toGroovy(newData, eval(name));
            if (onSuccess !== null && onSuccess !== undefined) {
                onSuccess(successData);
            }
        })
        .fail(function(error) {
            if (onFailure !== null && onFailure !== undefined) {
                onFailure(error);
            }
        });
    */}

    @GsNative
    static void createComponent(componentClass, name) {/*
        var component = Object.create(HTMLElement.prototype);
        component.createdCallback = function() {
            var shadow = this.createShadowRoot();
            var content = this.textContent;
            var attrs = this.attributes; //name and value
            var map = {shadowRoot: shadow, content: content};
            if (attrs && attrs.length > 0) {
                for (var i = 0; i < attrs.length; i++) {
                    var element = attrs[i];
                    map[element.name] = element.value;
                }
            }
            GrooscriptGrails.register(componentClass(map)).render();
        };
        document.registerElement(name, {prototype: component});
    */}

    static findComponentById(String id) {
        components.find { key, value ->
            value.id == id
        }?.value
    }
}

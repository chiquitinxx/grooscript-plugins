package org.grooscript.grails.util

import org.grooscript.asts.GsNative

/**
 * User: jorgefrancoleza
 * Date: 22/09/13
 */
class GrooscriptGrails {

    static remoteUrl
    static controllerRemoteDomain = 'remoteDomain'
    static actionRemoteDomain = 'doAction'

    static final GRAILS_PROPERTIES = ['url', 'class', 'clazz', 'gsName',
        'transients', 'constraints', 'mapping', 'hasMany', 'belongsTo', 'validationSkipMap',
        'gormPersistentEntity', 'properties', 'gormDynamicFinders', 'all', 'domainClass', 'attached',
        'validationErrorsMap', 'dirtyPropertyNames', 'errors', 'dirty', 'count']

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
    static void sendClientMessage(String channel, message) {/*
        var sendMessage = message;
        if (!gs.isGroovyObj(message)) {
            sendMessage = gs.toGroovy(message);
        }
        gsEvents.sendMessage(channel, sendMessage);
    */}

    @GsNative
    static void sendWebsocketMessage(String channel, message) {/*
        var sendMessage = message;
        if (gs.isGroovyObj(message)) {
            sendMessage = gs.toJavascript(message);
        }
        websocketClient.send(channel, {}, JSON.stringify(sendMessage));
    */}

    @GsNative
    static void doRemoteCall(String controller, String action, params, onSuccess, onFailure) {/*
        var url = GrooscriptGrails.remoteUrl;
        url = url + '/' + controller;
        if (domainAction != null) {
            url = url + '/' + domainAction;
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
}

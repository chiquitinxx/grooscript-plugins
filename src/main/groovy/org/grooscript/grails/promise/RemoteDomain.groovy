package org.grooscript.grails.promise

import org.grooscript.grails.util.GrooscriptGrails

/**
 * Created by jorge on 24/12/13.
 */
class RemoteDomain implements GsPromise {

    def action
    def url
    def data
    def onSuccess
    def onFail
    def name

    def closure = { ->
        def remoteData = [action: action, url: url, data: data]
        GrooscriptGrails.remoteDomainAction(remoteData, onSuccess, onFail, name)
    }

    @Override
    def then(Closure success, Closure fail) {
        onSuccess = success
        onFail = fail
        closure.delegate = this
        closure()
    }
}

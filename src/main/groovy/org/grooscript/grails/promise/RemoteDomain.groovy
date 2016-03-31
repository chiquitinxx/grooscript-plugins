package org.grooscript.grails.promise

import groovy.transform.TypeChecked
import org.grooscript.grails.util.GrooscriptGrails

/**
 * Created by jorge on 24/12/13.
 */
@TypeChecked
class RemoteDomain implements GsPromise {

    def action
    def url
    def data
    def onSuccess
    def onFail
    def name

    Closure closure = { ->
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

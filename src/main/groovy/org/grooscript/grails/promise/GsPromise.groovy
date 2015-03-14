package org.grooscript.grails.promise

/**
 * Created by jorge on 22/12/13.
 */
interface GsPromise {
    def then(Closure success, Closure fail)
}

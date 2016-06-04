package org.grooscript.grails.core.promise

interface GsPromise {
    def then(Closure success, Closure fail)
}

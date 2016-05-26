package org.grooscript.grails.promise

/**
 * @author Jorge Franco <jorge.franco@osoco.es>
 */
interface GsPromise {
    def then(Closure success, Closure fail)
}

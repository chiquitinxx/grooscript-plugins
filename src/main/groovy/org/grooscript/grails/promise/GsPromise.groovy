package org.grooscript.grails.promise

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @author jorge
 */
@ParametersAreNonnullByDefault
interface GsPromise {
    def then(Closure success, Closure fail)
}

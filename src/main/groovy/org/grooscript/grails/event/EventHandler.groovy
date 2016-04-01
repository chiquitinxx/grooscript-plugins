package org.grooscript.grails.event

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @author Jorge Franco
 * Date: 09/08/13
 */
@ParametersAreNonnullByDefault
interface EventHandler {
    void sendMessage(String channel, Map data)
    void onEvent(String channel, Closure action)
    void close()
}

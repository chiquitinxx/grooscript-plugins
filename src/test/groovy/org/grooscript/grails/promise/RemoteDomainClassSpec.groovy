package org.grooscript.grails.promise

import org.grooscript.grails.util.GrooscriptGrails
import spock.lang.Specification

/**
 * Created by jorge on 22/12/13.
 */
class RemoteDomainClassSpec extends Specification {

    private static final SUCCESS_ACTION = { -> 'ok' }
    private static final FAILURE_ACTION = { -> 'ko' }
    private static final NAME = 'name'
    private static final DOMAIN_CLASS_NAME = 'RemoteItem'
    private static final URL = '/remoteItem'

    def 'initial properties of remote domain class'() {
        given:
        def remoteItem = remoteDomainClassInstance

        expect:
        remoteItem.gsName == 'RemoteItem'
        remoteItem.name == null
        remoteItem.url == URL
        remoteItem.id == null
        remoteItem.version == 0
    }

    def 'create a remote domain instance'() {
        given:
        GroovySpy(GrooscriptGrails, global: true)
        def remoteItem = remoteDomainClassInstance

        when:
        remoteItem.name = NAME
        remoteItem.save().then(SUCCESS_ACTION, FAILURE_ACTION)

        then:
        1 * GrooscriptGrails.remoteDomainAction(
                [action: 'create', url: URL, data: properties],
                SUCCESS_ACTION, FAILURE_ACTION, DOMAIN_CLASS_NAME)
        1 * GrooscriptGrails.getRemoteDomainClassProperties(remoteItem) >> properties
        0 * _
    }

    def 'update a remote domain instance'() {
        given:
        GroovySpy(GrooscriptGrails, global: true)
        def remoteItem = remoteDomainClassInstance

        when:
        remoteItem.id = 5
        remoteItem.save().then(SUCCESS_ACTION, FAILURE_ACTION)

        then:
        1 * GrooscriptGrails.remoteDomainAction(
                [action: 'update', url: URL, data: properties],
                SUCCESS_ACTION, FAILURE_ACTION, DOMAIN_CLASS_NAME)
        1 * GrooscriptGrails.getRemoteDomainClassProperties(remoteItem) >> properties
        0 * _
    }

    def 'get remote domain instance'() {
        given:
        GroovySpy(GrooscriptGrails, global: true)
        def remoteItem = remoteDomainClassInstance

        when:
        remoteItem.get(1).then(SUCCESS_ACTION, FAILURE_ACTION)

        then:
        1 * GrooscriptGrails.remoteDomainAction(
                [action: 'read', url: URL, data:[id: 1L]], SUCCESS_ACTION, FAILURE_ACTION, DOMAIN_CLASS_NAME)
        0 * _
    }

    def 'list remote domain class'() {
        given:
        GroovySpy(GrooscriptGrails, global: true)
        def remoteItem = remoteDomainClassInstance

        when:
        remoteItem.list().then(SUCCESS_ACTION, FAILURE_ACTION)

        then:
        1 * GrooscriptGrails.remoteDomainAction(
                [action: 'list', url: URL, data: [:]], SUCCESS_ACTION, FAILURE_ACTION, DOMAIN_CLASS_NAME)
        0 * _
    }

    def 'list remote domain class with params'() {
        given:
        GroovySpy(GrooscriptGrails, global: true)
        def remoteItem = remoteDomainClassInstance

        when:
        remoteItem.list(max: 100).then(SUCCESS_ACTION, FAILURE_ACTION)

        then:
        1 * GrooscriptGrails.remoteDomainAction(
                [action: 'list', url: URL, data: [max: 100]], SUCCESS_ACTION, FAILURE_ACTION, DOMAIN_CLASS_NAME)
        0 * _
    }

    def 'delete remote domain class'() {
        given:
        GroovySpy(GrooscriptGrails, global: true)
        def remoteItem = remoteDomainClassInstance

        when:
        remoteItem.id = 2
        remoteItem.delete().then(SUCCESS_ACTION, FAILURE_ACTION)

        then:
        1 * GrooscriptGrails.remoteDomainAction(
                [action: 'delete', url: URL, data:[id: 2]], SUCCESS_ACTION, FAILURE_ACTION, DOMAIN_CLASS_NAME)
        0 * _
    }

    private getRemoteDomainClassInstance() {
        GroovyClassLoader invoker = new GroovyClassLoader()
        def clazz = invoker.parseClass('@org.grooscript.grails.remote.RemoteDomainClass \n' +
                "@grails.rest.Resource(uri='${URL}')\n" +
                "class ${DOMAIN_CLASS_NAME} { String name }")
        clazz.newInstance()
    }

    private getProperties() {
        [id: null, name: NAME, version: 0]
    }
}

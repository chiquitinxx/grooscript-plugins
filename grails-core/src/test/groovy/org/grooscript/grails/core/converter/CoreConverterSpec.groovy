package org.grooscript.grails.core.converter

import spock.lang.Specification

class CoreConverterSpec extends Specification {

    def 'convert component'() {
        when:
        def result = converter.convertComponent('class MyComponent { def render() {} }', [:])

        then:
        result.contains 'gSobject.cId = null'
    }

    def 'convert templates'() {
        when:
        def result = converter.convert('''def gsTextHtml = { data -> HtmlBuilder.build { builderIt ->
    [1, 2].each { println it }
}}''', [mainContextScope: ['HtmlBuilder']])
        then:
        result == '''var gsTextHtml = function(data) {
  return gs.mc(HtmlBuilder,"build",[function(builderIt) {
    return gs.mc(gs.list([1 , 2]),"each",[function(it) {
      return gs.println(it);
    }]);
  }]);
};
'''
    }

    private Converter converter = new CoreConverter()
}

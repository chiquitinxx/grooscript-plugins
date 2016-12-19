/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.gradle.util

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TagsFinderSpec extends Specification {

    void 'find tags in text'() {
        when:
        def result = finder.getTags('hello', text)

        then:
        result == ['hello']

        where:
        text << [
                '<hello></hello>',
                '                 <hello></hello>',
                '<hello      />'
        ]
    }

    void 'not finding tags'() {
        when:
        def result = finder.getTags('hello', text)

        then:
        result == []

        where:
        text << [
                '<notHello></hello>',
                '</hello>',
                null,
                ''
        ]
    }

    void 'get attributes of the tag'() {
        when:
        def result = finder.getAttributes('hello', text)

        then:
        result == [[tag: 'hello', number: '8', data: 'grooscript']]

        where:
        text << [
                '<hello   number=\'8\'  data=\'grooscript\'></hello>',
                '          \r \n \t <hello   number=\'8\'  data=\'grooscript\'></hello>     \r',
                '<hello number=\"8\"  data=\"grooscript\"     />',
                '<hello number=\"8\"  data=\'grooscript\'/>',
                '<hello  \n \t \r number=\"8\" \n\r  data=\'grooscript\'/>',
                '<hello  number = \"8\" data   =    \'grooscript\'/>',
                '<hello   number=\'8\'  data=\'grooscript\'> other=\'uhm\' </hello>',
        ]
    }

    private TagsFinder finder = new TagsFinder()
}

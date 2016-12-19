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

import java.util.regex.Matcher

class TagsFinder {

    private final static String BEGIN = '<'
    private final static String END = '>'
    private final static String SLASH = '/'
    private final static String EQUALS = '='
    private final static String BLANK = ' '

    List<String> getTags(String tag, String text) {

        Matcher matcher = text =~ /\$BEGIN$tag/
        matcher.collect {
            it.toString().substring(1)
        }
    }

    List<Map> getAttributes(String tag, String text) {
        Matcher matcher = text =~ /\$BEGIN$tag[^\$END]*/
        matcher.collect {
            String attributes = it.toString().substring(1 + tag.length()).trim()
            [tag: tag] << getTagAttributes(attributes)
        }
    }

    private Map getTagAttributes(String attributes) {
        Map result = [:]
        if (attributes) {
            if (attributes.endsWith(SLASH))
                attributes = attributes.substring(0, attributes.size() - 1)
            List<String> wordsWithValue = getWordsWithValue(attributes)
            wordsWithValue.each { att ->
                int equals = att.indexOf(EQUALS)
                if (equals > 0)
                    result.put(att.substring(0, equals), removeQuotes(att.substring(equals + 1)))
            }
        }
        result
    }

    private List<String> getWordsWithValue(String attributes) {
        String result = attributes
        while (result.matches(/.*$EQUALS\s.*/))
            result = result.replaceAll(/$EQUALS\s/, EQUALS)
        while (result.matches(/.*\s$EQUALS.*/))
            result = result.replaceAll(/\s$EQUALS/, EQUALS)
        result.split(BLANK)
    }

    private String removeQuotes(String text) {
        text.replaceAll(/'/, '').replaceAll(/"/, '')
    }
}

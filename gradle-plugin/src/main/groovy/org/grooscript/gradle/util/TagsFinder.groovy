package org.grooscript.gradle.util

import java.util.regex.Matcher

/**
 * Created by jorgefrancoleza on 13/12/16.
 */
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

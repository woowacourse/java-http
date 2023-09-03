package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryString {

    private final Map<String, String> queryStrings = new HashMap<>();

    private static QueryString emptyQueryString() {
        return new QueryString();
    }

    public static QueryString from(final String originRequestURI) {
        final QueryString queryString = new QueryString();
        if (!originRequestURI.contains("?")) {
            return emptyQueryString();
        }
        final String[] split = originRequestURI.split("\\?");
        if (split.length < 2) {
            return emptyQueryString();
        }
        final String splitQueryStrings = split[1];
        addKeyValue(splitQueryStrings, queryString);
        return queryString;
    }

    private static void addKeyValue(final String splitQueryStrings, final QueryString queryString) {
        final String[] splitQueryString = splitQueryStrings.split("&");
        for (final String keyValue : splitQueryString) {
            addQueryStringKeyValueBySplitString(queryString, keyValue);
        }
    }

    private static void addQueryStringKeyValueBySplitString(final QueryString queryString, final String keyValue) {
        final String[] splitKeyValue = keyValue.split("=");
        if (splitKeyValue.length < 2) {
            queryString.queryStrings.put(splitKeyValue[0], "");
            return;
        }
        queryString.queryStrings.put(splitKeyValue[0], splitKeyValue[1]);
    }

    public String get(final String key) {
        return queryStrings.get(key);
    }

    private QueryString() {
    }
}

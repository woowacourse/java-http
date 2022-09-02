package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryStrings {

    private static final char QUERY_STRING_STANDARD = '?';
    private static final String QUERY_STRINGS_BOUNDARY = "&";
    private static final String KEY_VALUE_BOUNDARY = "=";
    private static final int INDEX_NOT_FOUND = -1;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> value;

    public QueryStrings(final String uri) {
        this.value = extractQueryString(uri);
    }

    private Map<String, String> extractQueryString(final String uri) {
        int index = uri.lastIndexOf(QUERY_STRING_STANDARD);
        if (index == INDEX_NOT_FOUND) {
            return new HashMap<>();
        }

        final String queryStringText = uri.substring(index + 1);
        return initialQueryStringMap(queryStringText);
    }

    private Map<String, String> initialQueryStringMap(final String queryStringText) {
        final Map<String, String> result = new HashMap<>();
        final String[] queryStrings = queryStringText.split(QUERY_STRINGS_BOUNDARY);

        for (String queryString : queryStrings) {
            String[] keyValuePair = queryString.split(KEY_VALUE_BOUNDARY);
            result.put(keyValuePair[KEY_INDEX], keyValuePair[VALUE_INDEX]);
        }

        return result;
    }

    public String findByKey(final String key) {
        return value.get(key);
    }
}

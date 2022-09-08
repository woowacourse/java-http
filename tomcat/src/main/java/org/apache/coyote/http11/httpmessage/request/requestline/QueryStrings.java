package org.apache.coyote.http11.httpmessage.request.requestline;

import java.util.HashMap;
import java.util.Map;

public class QueryStrings {

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private static final String QUERY_STRINGS = "?";
    private static final String CONTENT_SPLITTER = "&";
    private static final String NAME_AND_VALUE_SPLITTER = "=";

    private final Map<String, String> queryStrings;

    public QueryStrings(final String resourcePath) {
        queryStrings = new HashMap<>();
        final int index = resourcePath.indexOf(QUERY_STRINGS);

        for (final String queryString : resourcePath.substring(index + 1).split(CONTENT_SPLITTER)) {
            final String name = queryString.split(NAME_AND_VALUE_SPLITTER)[NAME_INDEX];
            final String value = queryString.split(NAME_AND_VALUE_SPLITTER)[VALUE_INDEX];
            queryStrings.put(name, value);
        }
    }
}

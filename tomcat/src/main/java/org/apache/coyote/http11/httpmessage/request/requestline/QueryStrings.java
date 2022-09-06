package org.apache.coyote.http11.httpmessage.request.requestline;

import java.util.HashMap;
import java.util.Map;

public class QueryStrings {

    private final Map<String, String> queryStrings;

    public QueryStrings(final String resourcePath) {
        queryStrings = new HashMap<>();
        final int index = resourcePath.indexOf("?");

        for (final String queryString : resourcePath.substring(index + 1).split("&")) {
            final String name = queryString.split("=")[0];
            final String value = queryString.split("=")[1];
            queryStrings.put(name, value);
        }
    }

    public String getValue(final String name) {
        return queryStrings.get(name);
    }
}

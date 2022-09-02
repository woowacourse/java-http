package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryStrings {

    private final Map<String, String> values = new HashMap<>();

    public QueryStrings(final String uri) {
        String queryString = uri.substring(uri.indexOf("?") + 1);
        String[] queries = queryString.split("&");
        for (String query : queries) {
            String[] split = query.split("=");
            values.put(split[0], split[1]);
        }
    }

    public String find(final String key) {
        return values.get(key);
    }
}

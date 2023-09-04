package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryStrings {

    private final Map<String, String> queryStrings;

    public QueryStrings() {
        this.queryStrings = new HashMap<>();
    }

    public void add(final String key, final String value) {
        queryStrings.put(key, value);
    }

    public Map<String, String> getQueryStrings() {
        return queryStrings;
    }
}

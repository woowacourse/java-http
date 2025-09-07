package org.apache.coyote.http11.httpRequest;

import java.util.Map;

public class QueryStrings {

    private final Map<String, String> queryStrings;

    public QueryStrings(Map<String, String> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public String get(String key) {
        return this.queryStrings.get(key);
    }
}

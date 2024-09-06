package org.apache.coyote.http11.request;

import java.util.Map;

public class QueryString {

    private final Map<String, String> queryStrings;

    public QueryString(Map<String, String> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public String getValue(String key) {
        return queryStrings.get(key);
    }
}

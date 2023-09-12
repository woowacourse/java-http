package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryString {

    private final Map<String, String> queryStrings;

    public QueryString(Map<String, String> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public static QueryString from(String queryString) {
        Map<String, String> queryStrings = new HashMap<>();

        String[] splitQueryString = queryString.split("&");
        for (String it : splitQueryString) {
            int index = it.indexOf("=");
            String key = it.substring(0, index);
            String value = it.substring(index + 1);
            queryStrings.put(key, value);
        }
        return new QueryString(queryStrings);
    }

    public static QueryString empty() {
        return new QueryString(Collections.emptyMap());
    }
}

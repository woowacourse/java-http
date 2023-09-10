package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class QueryString {

    private static final String QUERY_STRING_SYMBOL = "?";

    private Map<String, String> values;

    private QueryString(Map<String, String> values) {
        this.values = values;
    }

    public static QueryString of(String uri) {
        Map<String, String> map = new HashMap<>();
        final int queryIndex = uri.indexOf(QUERY_STRING_SYMBOL);
        final String[] queryParameters = uri.substring(queryIndex + 1).split("&");
        for (String queryParameter : queryParameters) {
            final String[] queryKeyAndValue = queryParameter.split("=");
            if (queryKeyAndValue.length != 2) {
                throw new IllegalArgumentException("잘못된 Query String 입니다.");
            }
            final String key = queryKeyAndValue[0];
            final String value = queryKeyAndValue[1];
            map.put(key, value);
        }
        return new QueryString(map);
    }
}

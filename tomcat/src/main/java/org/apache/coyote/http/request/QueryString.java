package org.apache.coyote.http.request;

import java.util.Map;

public class QueryString {

    private final Map<String, String> values;

    public QueryString(final Map<String, String> values) {
        this.values = values;
    }

    public String get(final String key) {
        if (!values.containsKey(key)) {
            throw new IllegalStateException("HttpRequest 에 존재하지 않는 queryString 입니다.");
        }
        return values.get(key);
    }
}

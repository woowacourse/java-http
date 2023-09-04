package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {

    private final Map<String, String> params;

    public static QueryString from(final String queryString) {
        if (queryString.isEmpty()) {
            return new QueryString(new HashMap<>());
        }
        final Map<String, String> params = Arrays.stream(queryString.split("&"))
                .map(string -> Arrays.asList(string.split("=")))
                .collect(Collectors.toMap(data -> data.get(0), data -> data.get(1), (a, b) -> b));
        return new QueryString(params);
    }

    private QueryString(final Map<String, String> params) {
        this.params = params;
    }

    public boolean hasNoQueryString() {
        return params.isEmpty();
    }

    public String get(final String key) {
        return params.get(key);
    }

}

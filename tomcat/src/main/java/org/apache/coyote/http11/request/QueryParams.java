package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParams {

    private final Map<String, String> values;

    private QueryParams(final Map<String, String> values) {
        this.values = values;
    }

    public static QueryParams from(final String queryString) {
        final Map<String, String> values = Arrays.stream(queryString.split("&"))
                .map(param -> param.split("=", 2))
                .filter(param -> param.length == 2)
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1]
                ));
        return new QueryParams(values);
    }

    public static QueryParams ofEmpty() {
        return new QueryParams(Map.of());
    }

    public String get(String key) {
        return values.get(key);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }
}

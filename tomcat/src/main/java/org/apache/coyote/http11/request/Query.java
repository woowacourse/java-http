package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class Query {
    private final Map<String, String> query;

    private Query(final Map<String, String> query) {
        this.query = query;
    }

    public static Query from(final String query) {
        return new Query(Arrays.stream(query.split("&"))
                .map(data -> data.split("="))
                .collect(Collectors.toMap(
                        data -> data[0],
                        data -> data[1]
                )));
    }

    public static Query empty() {
        return new Query(Collections.emptyMap());
    }

    public String getQueryValue(String key) {
        return query.get(key);
    }
}

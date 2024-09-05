package org.apache.coyote.request.uri;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

public class QueryParams {

    private final Map<String, String> queryParams;

    public QueryParams(String queryParams) {
        this.queryParams = Arrays.stream(queryParams.split("&"))
                .map(params -> params.split("="))
                .collect(toMap(p -> p[0], p -> p[1]));
    }

    public String getValue(String key) {
        if (!queryParams.containsKey(key)) {
            throw new IllegalArgumentException("Query param not found");
        }
        return queryParams.get(key);
    }
}

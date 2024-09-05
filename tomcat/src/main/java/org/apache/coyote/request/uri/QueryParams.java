package org.apache.coyote.request.uri;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class QueryParams {

    public static final QueryParams EMPTY = new QueryParams(Collections.emptyMap());

    private final Map<String, String> queryParams;

    public static QueryParams from(String queryParams) {
        return Arrays.stream(queryParams.split("&"))
                .map(params -> params.split("=", 2))
                .collect(collectingAndThen(toMap(p -> p[0], p -> p[1]), QueryParams::new));
    }

    private QueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public String getValue(String key) {
        if (!queryParams.containsKey(key)) {
            throw new IllegalArgumentException("Query param not found");
        }
        return queryParams.get(key);
    }
}

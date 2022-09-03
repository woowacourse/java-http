package org.apache.coyote.http11.model.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParams {

    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> queryParams;

    public QueryParams(final Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static QueryParams empty() {
        return new QueryParams(new HashMap<>());
    }

    public static QueryParams of(final String query) {
        return new QueryParams(
                Arrays.stream(query.split(PARAM_SEPARATOR))
                        .map(it -> it.split(KEY_VALUE_SEPARATOR))
                        .collect(Collectors.toMap(it -> it[KEY_INDEX], it -> it[VALUE_INDEX]))
        );
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}

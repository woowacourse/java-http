package org.apache.coyote.request;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

public class QueryParams {

    private static final String QUERY_STRING_DELIMITER = "&";

    private final List<QueryParam> queryParams;

    public QueryParams(final List<QueryParam> queryParams) {
        this.queryParams = queryParams;
    }

    public static QueryParams from(String queryParams) {
        return new QueryParams(Stream.of(queryParams.split(QUERY_STRING_DELIMITER))
                .map(QueryParam::from)
                .collect(toList()));
    }

    public String getValueFromKey(String key) {
        return queryParams.stream()
                .filter(it -> it.isSameKey(key))
                .findAny()
                .map(QueryParam::getValue)
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 키입니다."));
    }
}

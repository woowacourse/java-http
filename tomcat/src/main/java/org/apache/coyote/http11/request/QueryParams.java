package org.apache.coyote.http11.request;

import java.util.List;
import java.util.stream.Stream;

public class QueryParams {

    private final List<QueryParam> params;

    public QueryParams(List<QueryParam> params) {
        this.params = params;
    }

    public static QueryParams from(String queryParams) {
        return new QueryParams(
                Stream.of(queryParams.split("&"))
                        .map(QueryParam::from)
                        .toList()
        );
    }

    public String getValue(String key) {
        return params.stream()
                .filter(param -> param.isSameKey(key))
                .findAny()
                .map(QueryParam::getValue)
                .orElseThrow(IllegalArgumentException::new);
    }
}

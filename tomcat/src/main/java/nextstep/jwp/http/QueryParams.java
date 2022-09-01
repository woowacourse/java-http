package nextstep.jwp.http;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryParams {

    private static final String QUERY_PARAMS_SEPARATOR = "&";

    private final List<QueryParam> queryParams;

    public QueryParams(List<QueryParam> queryParams) {
        this.queryParams = queryParams;
    }

    public static QueryParams from(String queryParams) {
        return new QueryParams(Stream.of(queryParams.split(QUERY_PARAMS_SEPARATOR))
            .map(QueryParam::from)
            .collect(Collectors.toList()));
    }

    public String get(String key) {
        return queryParams.stream()
            .filter(it -> it.same(key))
            .findFirst()
            .map(QueryParam::getValue)
            .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 키 입니다."));
    }

    public static QueryParams empty() {
        return new QueryParams(List.of());
    }
}

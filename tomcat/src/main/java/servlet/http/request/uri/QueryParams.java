package servlet.http.request.uri;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class QueryParams {

    protected static final QueryParams EMPTY = new QueryParams(Collections.emptyMap());

    private final Map<String, String> queryParams;

    private QueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    protected static QueryParams from(String queryParams) {
        return Arrays.stream(queryParams.split("&"))
                .map(params -> params.split("=", 2))
                .collect(collectingAndThen(toMap(p -> p[0], p -> p[1]), QueryParams::new));
    }

    protected String get(String key) {
        if (!queryParams.containsKey(key)) {
            throw new IllegalArgumentException("Query parameter가 존재하지 않습니다.");
        }
        return queryParams.get(key);
    }

    protected boolean existQueryParams() {
        return !queryParams.isEmpty();
    }
}

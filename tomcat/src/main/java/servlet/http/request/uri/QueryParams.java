package servlet.http.request.uri;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class QueryParams {

    private static final QueryParams EMPTY = new QueryParams(Collections.emptyMap());
    private static final String PARAMS_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int LIMIT = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> queryParams;

    private QueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    protected static QueryParams from(String queryParams) {
        if (queryParams == null || queryParams.isBlank()) {
            return EMPTY;
        }
        return Arrays.stream(queryParams.split(PARAMS_DELIMITER))
                .map(params -> params.split(KEY_VALUE_DELIMITER, LIMIT))
                .collect(collectingAndThen(toMap(p -> p[KEY_INDEX], p -> p[VALUE_INDEX]), QueryParams::new));
    }

    protected String get(String key) {
        validateNotEmptyParams();
        if (!queryParams.containsKey(key)) {
            throw new IllegalArgumentException("Query parameter에 해당 key가 존재하지 않습니다. key: %s".formatted(key));
        }
        return queryParams.get(key);
    }

    private void validateNotEmptyParams() {
        if (!existQueryParams()) {
            throw new IllegalArgumentException("Query parameter가 비어있습니다.");
        }
    }

    protected boolean existQueryParams() {
        return !queryParams.isEmpty();
    }
}

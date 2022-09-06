package org.apache.coyote.http11.request.header;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private static final String QUERY_DELIMITER = "&";
    private static final String PARAMS_DELIMITER = "=";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> value;

    private QueryParams(final Map<String, String> value) {
        this.value = value;
    }

    public static QueryParams from(final String query) {
        final Map<String, String> params = new HashMap<>();

        final String[] paramElements = query.split(QUERY_DELIMITER);
        for (final String param : paramElements) {
            final String[] paramElement = param.split(PARAMS_DELIMITER);

            validateParamIsNotDuplicated(params, paramElement[KEY]);
            params.put(paramElement[KEY], paramElement[VALUE]);
        }
        return new QueryParams(params);
    }

    public static QueryParams empty() {
        return new QueryParams(new HashMap<>());
    }

    private static void validateParamIsNotDuplicated(final Map<String, String> params, final String key) {
        if (params.containsKey(key)) {
            throw new IllegalArgumentException(String.format("파라미터가 중복적으로 입력되었습니다. [%s]", key));
        }
    }

    public Map<String, String> getValue() {
        return Map.copyOf(value);
    }
}

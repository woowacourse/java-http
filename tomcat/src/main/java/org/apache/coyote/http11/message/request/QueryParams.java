package org.apache.coyote.http11.message.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QueryParams {

    private static final String PARAM_VALUE_DELIMITER = "=";
    private static final String PARAMS_DELIMITER = "&";

    private final Map<String, String> paramsWithValue;

    private QueryParams(final Map<String, String> paramsWithValue) {
        this.paramsWithValue = paramsWithValue;
    }

    public static QueryParams from(final RequestLine requestLine) {
        final Optional<String> queryString = requestLine.parseQueryString();
        final Map<String, String> paramsWithValue = queryString.map(QueryParams::mapToQueryParamMap)
            .orElse(Collections.emptyMap());

        return new QueryParams(paramsWithValue);
    }

    private static Map<String, String> mapToQueryParamMap(final String queryString) {
        final Map<String, String> paramsWithValue = new HashMap<>();
        final String[] params = queryString.split(PARAMS_DELIMITER);

        for (final String param : params) {
            final String[] paramWithValue = param.split(PARAM_VALUE_DELIMITER);
            paramsWithValue.put(paramWithValue[0], paramWithValue[1]);
        }
        return paramsWithValue;
    }

    public String getValueOf(final String field) {
        return paramsWithValue.get(field);
    }

    public Map<String, String> getParamsWithValue() {
        return new HashMap<>(paramsWithValue);
    }
}

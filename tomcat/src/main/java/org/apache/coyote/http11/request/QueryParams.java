package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class QueryParams {

    private static final String QUERY_PARAM_SEPARATOR = "&";
    private static final String QUERY_PARAM_KEY_VALUE_SEPARATOR = "=";
    private static final int RAW_QUERY_PARAM_KEY_INDEX = 0;
    private static final int RAW_QUERY_PARAM_VALUE_INDEX = 1;
    private final Map<String, String> queryParams;

    private QueryParams(final Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static QueryParams from(final String queryString) {
        final String[] rawQueryParams = queryString.split(QUERY_PARAM_SEPARATOR);
        final Map<String, String> queryParams = new HashMap<>();
        for (String queryParam : rawQueryParams) {
            final String[] queryParamKeyValue = queryParam.split(QUERY_PARAM_KEY_VALUE_SEPARATOR);
            queryParams.put(
                    queryParamKeyValue[RAW_QUERY_PARAM_KEY_INDEX],
                    queryParamKeyValue[RAW_QUERY_PARAM_VALUE_INDEX]
            );
        }

        return new QueryParams(queryParams);
    }

    public static QueryParams empty() {
        return new QueryParams(new HashMap<>());
    }

    public Optional<String> getValue(final String key) {
        return Optional.ofNullable(queryParams.get(key));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueryParams)) {
            return false;
        }
        final QueryParams that = (QueryParams) o;
        return Objects.equals(queryParams, that.queryParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryParams);
    }
}

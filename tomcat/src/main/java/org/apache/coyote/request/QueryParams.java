package org.apache.coyote.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class QueryParams {

    private static final String QUERY_PARAM_START_CONDITION = "?";
    private static final String QUERY_PARAMS_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";

    private static final int PARAM_NAME_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    private final Map<String, String> params;

    private QueryParams(final Map<String, String> params) {
        this.params = params;
    }

    public static QueryParams empty() {
        return new QueryParams(new HashMap<>());
    }

    public static QueryParams from(final String queryParamNamesAndValues) {
        if (Objects.isNull(queryParamNamesAndValues) || queryParamNamesAndValues.isBlank()) {
            return empty();
        }

        final int paramStartIndex = queryParamNamesAndValues.indexOf(QUERY_PARAM_START_CONDITION) + 1;
        return new QueryParams(collectQueryParamMapping(queryParamNamesAndValues, paramStartIndex));
    }

    private static Map<String, String> collectQueryParamMapping(final String queryParamNamesAndValues, final int paramStartIndex) {
        return Arrays.stream(queryParamNamesAndValues
                        .substring(paramStartIndex)
                        .split(QUERY_PARAMS_DELIMITER))
                .map(queryParam -> queryParam.split(QUERY_PARAM_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        entry -> entry[PARAM_NAME_INDEX],
                        entry -> entry[PARAM_VALUE_INDEX])
                );
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public List<String> paramNames() {
        return new ArrayList<>(params.keySet());
    }

    public String getParamValue(final String name) {
        return params.getOrDefault(name, null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final QueryParams that = (QueryParams) o;
        return Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }

    @Override
    public String toString() {
        return "QueryParams{" +
               "params=" + params +
               '}';
    }
}

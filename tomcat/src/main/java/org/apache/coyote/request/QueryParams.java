package org.apache.coyote.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryParams {

    private static final String QUERY_PARAM_START_CONDITION = "?";
    private static final String QUERY_PARAMS_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";
    private static final int PARAM_NAME_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    private final Map<String, String> params = new HashMap<>();

    private QueryParams(final Map<String, String> params) {
        this.params.putAll(params);
    }

    public static QueryParams empty() {
        return new QueryParams(new HashMap<>());
    }

    public static QueryParams from(final String queryParamsWithValue) {
        final Map<String, String> mapping = new HashMap<>();

        final int paramStartIndex = queryParamsWithValue.indexOf(QUERY_PARAM_START_CONDITION) + 1;
        final String[] queryParams = queryParamsWithValue.substring(paramStartIndex).split(QUERY_PARAMS_DELIMITER);
        for (String queryParam : queryParams) {
            final String[] paramWithValue = queryParam.split(QUERY_PARAM_VALUE_DELIMITER);
            mapping.put(paramWithValue[PARAM_NAME_INDEX], paramWithValue[PARAM_VALUE_INDEX]);
        }

        return new QueryParams(mapping);
    }

    public List<String> paramNames() {
        return new ArrayList<>(params.keySet());
    }

    public String getParamValue(final String name) {
        return params.getOrDefault(name, null);
    }

    @Override
    public String toString() {
        return "QueryParams{" +
               "params=" + params +
               '}';
    }
}

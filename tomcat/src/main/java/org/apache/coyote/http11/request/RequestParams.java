package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestParams {

    public static final RequestParams EMPTY = new RequestParams(Map.of());
    private static final String PARAM_KEY_VALUE_SEPARATOR = "=";
    private static final String PARAM_SEPARATOR = "&";
    private static final int VALUE_INDEX = 1;
    private static final int KEY_INDEX = 0;

    private final Map<String, String> params;

    private RequestParams(Map<String, String> params) {
        this.params = params;
    }

    public static RequestParams parse(String queryParam) {
        if (!queryParam.contains(PARAM_KEY_VALUE_SEPARATOR)) {
            return EMPTY;
        }
        Map<String, String> collect = Arrays.stream(queryParam.split(PARAM_SEPARATOR))
                .map(query -> query.split(PARAM_KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(query -> query[KEY_INDEX], query -> query[VALUE_INDEX]));
        return new RequestParams(collect);
    }

    public String getParam(String key) {
        return params.getOrDefault(key, "");
    }
}

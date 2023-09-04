package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestParams {

    public static final RequestParams EMPTY = new RequestParams(Map.of());

    private final Map<String, String> params;

    private RequestParams(Map<String, String> params) {
        this.params = params;
    }

    public static RequestParams parse(String queryParam) {
        Map<String, String> collect = Arrays.stream(queryParam.split("&"))
                .map(query -> query.split("="))
                .collect(Collectors.toMap(query -> query[0], query -> query[1]));
        return new RequestParams(collect);
    }

    public String getParam(String key) {
        if (params.containsKey(key)) {
            return params.get(key);
        }
        return "";
    }
}

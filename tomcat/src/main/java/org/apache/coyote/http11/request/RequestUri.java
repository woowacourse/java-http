package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestUri {
    private static final String QUERY_PARAMETER_SIGN = "?";
    private static final int DEFAULT_QUERY_PARAMETER_PAIR_SIZE = 2;

    private final String path;
    private final Map<String, String> params;

    private RequestUri(final String path, final Map<String, String> params) {
        this.path = path;
        this.params = params;
    }

    public static RequestUri from(final String uri) {
        if (uri.contains(QUERY_PARAMETER_SIGN)) {
            final int index = uri.indexOf(QUERY_PARAMETER_SIGN);
            final String path = uri.substring(0, index);

            return new RequestUri(path, parseQueryParams(uri, index));
        }

        return new RequestUri(uri, Collections.emptyMap());
    }

    private static Map<String, String> parseQueryParams(final String uri, final int index) {
        final Map<String, String> params = new HashMap<>();
        final String queryString = uri.substring(index + 1);
        final String[] paramPairs = queryString.split("&");

        for (final String param : paramPairs) {
            addParamPair(params, param);
        }

        return params;
    }

    private static void addParamPair(final Map<String, String> params, final String param) {
        if (param == null || param.isBlank()) {
            return;
        }

        final String[] pair = param.split("=");
        if (pair.length != DEFAULT_QUERY_PARAMETER_PAIR_SIZE) {
            throw new IllegalArgumentException("올바른 Query Parameter 형식이 아닙니다.");
        }
        params.put(pair[0], pair[1]);
    }

    public String findParamValueByName(final String name) {
        return params.get(name);
    }

    public boolean containsQuery() {
        return !params.isEmpty();
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}

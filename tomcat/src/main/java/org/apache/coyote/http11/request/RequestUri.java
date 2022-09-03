package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {
    private final String path;
    private final Map<String, String> params;

    public RequestUri(final String path, final Map<String, String> params) {
        this.path = path;
        this.params = params;
    }

    public static RequestUri from(final String uri) {
        final Map<String, String> params = new HashMap<>();
        if (uri.contains("?")) {
            final int index = uri.indexOf("?");
            parseQueryParams(uri, params, index);
            return new RequestUri(uri.substring(0, index), params);
        }
        return new RequestUri(uri, params);
    }

    private static void parseQueryParams(final String uri, final Map<String, String> params, final int index) {
        final String queryString = uri.substring(index + 1);
        final String[] paramPairs = queryString.split("&");

        for (final String paramPair : paramPairs) {
            addParamPair(params, paramPair);
        }
    }

    private static void addParamPair(final Map<String, String> params, final String paramPair) {
        final String[] pair = paramPair.split("=");
        if (pair.length == 2) {
            params.put(pair[0], pair[1]);
        }
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getParamByName(final String name) {
        return params.get(name);
    }
}

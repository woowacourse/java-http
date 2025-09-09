package org.apache.coyote.http11.httprequest;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {

    private static final String QUERY_STRING_SEPARATOR = "?";
    private static final String QUERY_STRING_REGEX = "\\?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_SEPARATOR = "=";

    private final String requestPath;
    private final Map<String, String> queryParameters;

    public static RequestUri from(final String rawRequestUri) {
        final String[] requestUri = rawRequestUri.split(QUERY_STRING_REGEX);
        final String requestPath = requestUri[0];

        if (!rawRequestUri.contains(QUERY_STRING_SEPARATOR)) {
            return new RequestUri(requestPath, new HashMap<>());
        }

        final String queryString = requestUri[1];
        final String[] queryParameters = queryString.split(QUERY_PARAMETER_SEPARATOR);
        final Map<String, String> parameters = new HashMap<>();
        for (String parameter : queryParameters) {
            final String[] keyAndValue = parameter.split(QUERY_PARAMETER_KEY_VALUE_SEPARATOR);
            final String key = keyAndValue[0];
            String value = "";
            if (keyAndValue.length == 2) {
                value = keyAndValue[1];
            }
            parameters.put(key, value);
        }

        return new RequestUri(requestPath, parameters);
    }

    public boolean isPathEqualsTo(final String requestPath) {
        return this.requestPath.equals(requestPath);
    }

    public String getRequestPath() {
        return this.requestPath;
    }

    private RequestUri(final String requestPath, final Map<String, String> queryParameters) {
        this.requestPath = requestPath;
        this.queryParameters = queryParameters;
    }
}

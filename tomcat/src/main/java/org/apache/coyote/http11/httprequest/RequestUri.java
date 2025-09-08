package org.apache.coyote.http11.httprequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestUri {

    private static final String QUERY_STRING_SEPARATOR = "?";
    private static final String QUERY_STRING_REGEX = "\\?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_SEPARATOR = "=";

    private final String requestPath;
    private final Map<String, String> queryParameters;

    public static RequestUri from(final String rawRequestUri) {
        final String requestPath = rawRequestUri.split(QUERY_STRING_REGEX)[0];

        if (!rawRequestUri.contains(QUERY_STRING_SEPARATOR)) {
            return new RequestUri(requestPath, new HashMap<>());
        }

        final String queryString = rawRequestUri.split(QUERY_STRING_REGEX)[1];
        final String[] queryParameters = queryString.split(QUERY_PARAMETER_SEPARATOR);
        final Map<String, String> parameters = Arrays.stream(queryParameters)
                .map(param -> param.split(QUERY_PARAMETER_KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1],
                        (oldValue, newValue) -> newValue
                ));

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

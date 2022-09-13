package org.apache.coyote.http11.http.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestTarget {

    private static final String REQUEST_TARGET_DELIMITER = "\\?";
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_PARAM_DELIMITER = "=";
    private static final int URI_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private static final int QUERY_PARAM_KEY = 0;
    private static final int QUERY_PARAM_VALUE = 1;

    private final String uri;
    private final Map<String, String> queryParameters;

    private RequestTarget(final String uri, final Map<String, String> queryParameters) {
        this.uri = uri;
        this.queryParameters = queryParameters;
    }

    public static RequestTarget from(final String requestTarget) {
        validateRequestTarget(requestTarget);
        String uri = parseUri(requestTarget);
        Map<String, String> queryParameters = parseQueryString(requestTarget);
        return new RequestTarget(uri, queryParameters);
    }

    private static void validateRequestTarget(final String requestTarget) {
        if (!isValid(requestTarget)) {
            throw new IllegalArgumentException("Invalid request-target");
        }
    }

    private static String parseUri(final String requestTarget) {
        if (containsQueryString(requestTarget)) {
            return requestTarget.split(REQUEST_TARGET_DELIMITER)[URI_INDEX];
        }
        return requestTarget;
    }

    private static Map<String, String> parseQueryString(final String requestTarget) {
        if (containsQueryString(requestTarget)) {
            String queryString = requestTarget.split(REQUEST_TARGET_DELIMITER)[QUERY_STRING_INDEX];
            return Arrays.stream(queryString.split(QUERY_STRING_DELIMITER))
                    .map(line -> line.split(QUERY_PARAM_DELIMITER))
                    .collect(Collectors.toMap(line -> line[QUERY_PARAM_KEY], line -> line[QUERY_PARAM_VALUE]));
        }
        return new HashMap<>();
    }

    private static boolean isValid(final String requestTarget) {
        return Objects.nonNull(requestTarget) && requestTarget.startsWith("/");
    }

    private static boolean containsQueryString(final String requestTarget) {
        return requestTarget.contains("?");
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    @Override
    public String toString() {
        return uri + "?" + queryParameters;
    }
}

package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestTarget {

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
            return requestTarget.split("\\?")[0];
        }
        return requestTarget;
    }

    private static Map<String, String> parseQueryString(final String requestTarget) {
        if (containsQueryString(requestTarget)) {
            String queryString = requestTarget.split("\\?")[1];
            return Arrays.stream(queryString.split("&"))
                    .map(line -> line.split("="))
                    .collect(Collectors.toMap(line -> line[0], line -> line[1]));
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
}

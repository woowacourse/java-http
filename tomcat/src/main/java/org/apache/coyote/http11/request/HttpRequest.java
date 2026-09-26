package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public record HttpRequest(
        RequestLine requestLine,
        Map<String, String> headers,
        String body
) {
    public String header(final String name) {
        return headers.get(name);
    }

    public String path() {
        final String requestUri = requestLine.requestUri();
        final int queryStringIndex = requestUri.indexOf("?");
        if (queryStringIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, queryStringIndex);
    }

    public Map<String, String> params() {
        if (requestLine.method().equals("POST")) {
            return parseQueryString(body);
        }
        return extractQueryParams(requestLine.requestUri());
    }

    private Map<String, String> parseQueryString(final String queryString) {
        if (queryString.isEmpty()) {
            return Map.of();
        }
        final Map<String, String> params = new HashMap<>();
        final String[] pairs = queryString.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

    private Map<String, String> extractQueryParams(final String requestUri) {
        final int queryStringIndex = requestUri.indexOf("?");
        if (queryStringIndex == -1) {
            return Map.of();
        }
        final String queryString = requestUri.substring(queryStringIndex + 1);
        return parseQueryString(queryString);
    }
}

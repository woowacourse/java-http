package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public record HttpRequest(
        String method,
        String requestUri,
        Map<String, String> headers,
        String body
) {
    public String header(final String name) {
        return headers.get(name);
    }

    public String path() {
        int queryStringIndex = requestUri.indexOf("?");
        if (queryStringIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, queryStringIndex);
    }

    public Map<String, String> params() {
        if (method.equals("POST")) {
            return parseQueryString(body);
        }
        return extractQueryParams(requestUri);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        if (queryString.isEmpty()) {
            return Map.of();
        }
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

    private Map<String, String> extractQueryParams(final String requestUri) {
        int queryStringIndex = requestUri.indexOf("?");
        if (queryStringIndex == -1) {
            return Map.of();
        }
        String queryString = requestUri.substring(queryStringIndex + 1);
        return parseQueryString(queryString);
    }
}

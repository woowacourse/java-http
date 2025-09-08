package org.apache.coyote.http11.request;


import java.util.HashMap;
import java.util.Map;

public record RequestLine(String method,
                          String requestTarget,
                          String protocol) {

    public static RequestLine parse(final String requestLine) {
        final String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid HTTP request line: " + requestLine);
        }

        return new RequestLine(parts[0], parts[1], parts[2]);
    }

    public String parseResourcePath() {
        final int queryIndex = requestTarget().indexOf("?");
        String resourcePath = requestTarget();
        if (queryIndex != -1) {
            resourcePath = requestTarget().substring(0, queryIndex);
        }
        return resourcePath;
    }

    public Map<String, String> parseQuery() {
        final int queryIndex = requestTarget().indexOf("?");
        if (queryIndex == -1) {
            return new HashMap<>();
        }

        final String pathQuery = requestTarget().substring(queryIndex + 1);
        final String[] splitPathQuery = pathQuery.split("&");
        final Map<String, String> queries = new HashMap<>();
        for (String query : splitPathQuery) {
            final String[] keyValue = query.split("=");
            if (keyValue.length != 2) {
                return null;
            }
            queries.put(keyValue[0], keyValue[1]);
        }
        return queries;
    }
}

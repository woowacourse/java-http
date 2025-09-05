package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;

    public static HttpRequest from(final String rawRequest) {
        if (rawRequest == null || rawRequest.trim().isEmpty()) {
            return null;
        }

        final String[] lines = rawRequest.split("\r\n");
        if (lines.length == 0) {
            return null;
        }

        final String requestLine = lines[0];
        if (!requestLine.contains("HTTP/")) {
            return null;
        }

        final String[] parts = requestLine.split(" ");
        if (parts.length < 3) {
            return null;
        }

        final String method = parts[0];
        final String fullPath = parts[1];
        final String version = parts[2].replace("HTTP/", "");

        String path = fullPath;
        final Map<String, String> queryParams = new HashMap<>();
        
        final int queryIndex = fullPath.indexOf('?');
        if (queryIndex != -1) {
            path = fullPath.substring(0, queryIndex);
            final String queryString = fullPath.substring(queryIndex + 1);
            parseQueryString(queryString, queryParams);
        }

        final Map<String, String> headers = new HashMap<>();
        headers.put("version", version);

        for (int i = 1; i < lines.length; i++) {
            final String line = lines[i].trim();
            if (line.isEmpty()) {
                break;
            }
            final int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                final String headerName = line.substring(0, colonIndex).trim().toLowerCase();
                final String headerValue = line.substring(colonIndex + 1).trim();
                headers.put(headerName, headerValue);
            }
        }

        return new HttpRequest(method, path, headers, queryParams);
    }

    private static void parseQueryString(final String queryString, final Map<String, String> queryParams) {
        if (queryString == null || queryString.isEmpty()) {
            return;
        }
        
        final String[] pairs = queryString.split("&");
        for (final String pair : pairs) {
            final int equalIndex = pair.indexOf('=');
            if (equalIndex != -1) {
                final String key = pair.substring(0, equalIndex);
                final String value = pair.substring(equalIndex + 1);
                queryParams.put(key, value);
            }
        }
    }

    public String getQueryParam(final String name) {
        return queryParams.getOrDefault(name, "");
    }

    public String getHeader(final String name) {
        return headers.getOrDefault(name.toLowerCase(), "");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %s HTTP/%s\r\n", method, path, getHeader("version")));

        headers.forEach((key, value) -> {
            if ("version".equals(key)) {
                return;
            }
            sb.append(String.format("%s: %s\r\n",
                    key.substring(0, 1).toUpperCase() + key.substring(1), value));
        });

        sb.append("\r\n");
        return sb.toString();
    }
}

package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HttpRequest {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_PATH_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int MIN_REQUEST_LINE_PARTS = 3;
    private static final String VERSION_KEY = "version";

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

        final String[] requestLineParts = parseRequestLine(lines[0]);
        if (requestLineParts == null) {
            return null;
        }

        final String method = requestLineParts[HTTP_METHOD_INDEX];
        final String fullPath = requestLineParts[HTTP_PATH_INDEX];
        final String version = requestLineParts[HTTP_VERSION_INDEX].replace("HTTP/", "");

        final String path = extractPath(fullPath);
        final Map<String, String> queryParams = extractQueryParams(fullPath);
        final Map<String, String> headers = parseHeaders(lines, version);

        return new HttpRequest(method, path, headers, queryParams);
    }

    private static String[] parseRequestLine(final String requestLine) {
        if (!requestLine.contains("HTTP/")) {
            return null;
        }

        final String[] parts = requestLine.split(" ");
        return parts.length < MIN_REQUEST_LINE_PARTS ? null : parts;
    }

    private static String extractPath(final String fullPath) {
        final int queryIndex = fullPath.indexOf('?');
        return queryIndex != -1 ? fullPath.substring(0, queryIndex) : fullPath;
    }

    private static Map<String, String> extractQueryParams(final String fullPath) {
        final Map<String, String> queryParams = new HashMap<>();
        final int queryIndex = fullPath.indexOf('?');

        if (queryIndex != -1) {
            final String queryString = fullPath.substring(queryIndex + 1);
            parseQueryString(queryString, queryParams);
        }

        return queryParams;
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

    private static Map<String, String> parseHeaders(final String[] lines, final String version) {
        final Map<String, String> headers = new HashMap<>();
        headers.put(VERSION_KEY, version);

        for (int i = 1; i < lines.length; i++) {
            final String line = lines[i].trim();
            if (line.isEmpty()) {
                break;
            }
            parseHeaderLine(line, headers);
        }

        return headers;
    }

    private static void parseHeaderLine(final String line, final Map<String, String> headers) {
        final int colonIndex = line.indexOf(':');
        if (colonIndex <= 0) {
            return;
        }
        final String headerName = line.substring(0, colonIndex).trim().toLowerCase();
        final String headerValue = line.substring(colonIndex + 1).trim();
        headers.put(headerName, headerValue);
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
        appendRequestLine(sb);
        appendHeaders(sb);
        sb.append("\r\n");
        return sb.toString();
    }

    private void appendRequestLine(final StringBuilder sb) {
        sb.append(String.format("%s %s HTTP/%s\r\n", method, path, getHeader(VERSION_KEY)));
    }

    private void appendHeaders(final StringBuilder sb) {
        headers.forEach((key, value) -> {
            if (VERSION_KEY.equals(key)) {
                return;
            }
            sb.append(String.format("%s: %s\r\n", capitalizeFirstLetter(key), value));
        });
    }

    private String capitalizeFirstLetter(final String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

package org.apache.coyote.http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final Map<String, String> requestBody;
    private final Map<String, String> cookies;

    public static HttpRequest from(final String rawRequest) {
        if (rawRequest == null || rawRequest.trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP request cannot be null or empty");
        }

        final String[] parts = rawRequest.split("\r\n\r\n", 2);
        final String headerPart = parts[0];
        final String bodyPart = parts.length > 1 ? parts[1] : "";

        final String[] headerLines = headerPart.split("\r\n");
        if (headerLines.length == 0) {
            throw new IllegalArgumentException("HTTP headers cannot be empty");
        }

        final String[] requestLineParts = parseFirstHeaderLine(headerLines[0]);

        final HttpMethod method = HttpMethod.from(requestLineParts[HTTP_METHOD_INDEX]);
        final String fullPath = requestLineParts[HTTP_PATH_INDEX];
        final String version = requestLineParts[HTTP_VERSION_INDEX].replace("HTTP/", "");

        final String path = extractPath(fullPath);
        final Map<String, String> queryParams = extractQueryParams(fullPath);
        final Map<String, String> headers = parseHeaders(headerLines);

        final Map<String, String> requestBody = parseBody(bodyPart, headers.get("content-type"));
        final Map<String, String> cookies = parseCookies(headers.get("cookie"));

        return new HttpRequest(method, path, version, headers, queryParams, requestBody, cookies);
    }

    private static String[] parseFirstHeaderLine(final String requestLine) {
        if (!requestLine.contains("HTTP/")) {
            throw new IllegalArgumentException("HTTP request's first line must contain HTTP/");
        }

        final String[] parts = requestLine.trim().split("\\s+");
        if (parts.length < MIN_REQUEST_LINE_PARTS) {
            throw new IllegalArgumentException(
                    "HTTP request's first line must contain " + MIN_REQUEST_LINE_PARTS + " parts");
        }
        return parts;
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
                final String key = URLDecoder.decode(pair.substring(0, equalIndex), StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(pair.substring(equalIndex + 1), StandardCharsets.UTF_8);
                queryParams.put(key, value);
            }
        }
    }

    private static Map<String, String> parseHeaders(final String[] lines) {
        final Map<String, String> headers = new HashMap<>();

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

    private static Map<String, String> parseBody(final String body, final String contentType) {
        final Map<String, String> map = new HashMap<>();
        if (body == null || body.isEmpty()) {
            return map;
        }

        final String type = contentType == null ? "" : contentType.toLowerCase();
        if (type.contains("application/x-www-form-urlencoded")) {
            parseQueryString(body, map);
        }
        return map;
    }

    private static Map<String, String> parseCookies(final String cookieHeader) {
        final Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return cookies;
        }

        final String[] pairs = cookieHeader.split(";");
        for (final String pair : pairs) {
            final int equalIndex = pair.indexOf('=');
            if (equalIndex != -1) {
                final String key = pair.substring(0, equalIndex).trim();
                final String value = pair.substring(equalIndex + 1).trim();
                cookies.put(key, value);
            }
        }
        return cookies;
    }

    public String getQueryParam(final String name) {
        return queryParams.getOrDefault(name, "");
    }

    public String getHeader(final String name) {
        return headers.getOrDefault(name.toLowerCase(), "");
    }

    public String getBodyParam(final String name) {
        return requestBody.getOrDefault(name, "");
    }

    public String getCookie(final String name) {
        return cookies.getOrDefault(name, "");
    }

    private void appendRequestLine(final StringBuilder sb) {
        sb.append(String.format("%s %s HTTP/%s\r\n", method, path, version));
    }

    private void appendHeaders(final StringBuilder sb) {
        headers.forEach((key, value) ->
                sb.append(String.format("%s: %s\r\n", capitalizeFirstLetter(key), value)));
    }

    private String capitalizeFirstLetter(final String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        appendRequestLine(sb);
        appendHeaders(sb);
        sb.append("\r\n");
        return sb.toString();
    }
}

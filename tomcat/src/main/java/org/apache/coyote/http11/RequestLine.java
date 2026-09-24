package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final int REQUEST_LINE_TOKEN_COUNT = 3;

    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> queryParams;

    private RequestLine(final String method, final String path, final String version,
                        final Map<String, String> queryParams) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.queryParams = queryParams;
    }

    public static RequestLine from(final String requestLine) {
        final String[] tokens = requestLine.split(" ", -1);
        if (tokens.length != REQUEST_LINE_TOKEN_COUNT) {
            throw new IllegalArgumentException("올바르지 않은 HTTP 요청 라인입니다: " + requestLine);
        }

        final String method = tokens[0];
        final String uri = tokens[1];
        final String version = tokens[2];
        validateNotBlank(method, uri, version);

        return new RequestLine(method, extractPath(uri), version, extractQueryParams(uri));
    }

    private static void validateNotBlank(final String method, final String uri, final String version) {
        if (method.isBlank() || uri.isBlank() || version.isBlank()) {
            throw new IllegalArgumentException("HTTP 요청 라인의 구성 요소는 비어 있을 수 없습니다.");
        }
    }

    private static String extractPath(final String uri) {
        final int queryIndex = uri.indexOf('?');
        if (queryIndex == -1) {
            return uri;
        }
        return uri.substring(0, queryIndex);
    }

    private static Map<String, String> extractQueryParams(final String uri) {
        final int queryIndex = uri.indexOf('?');
        if (queryIndex == -1 || queryIndex == uri.length() - 1) {
            return Map.of();
        }

        final Map<String, String> queryParams = new HashMap<>();
        final String queryString = uri.substring(queryIndex + 1);
        for (String parameter : queryString.split("&")) {
            final String[] keyAndValue = parameter.split("=", 2);
            final String value = keyAndValue.length == 2 ? keyAndValue[1] : "";
            queryParams.put(keyAndValue[0], value);
        }
        return queryParams;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}

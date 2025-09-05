package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final String protocol;

    public RequestLine(final String requestLine) {
        final String[] requestLineParts = validateAndParseRequestLine(requestLine);
        this.method = requestLineParts[0];
        this.path = parsePath(requestLineParts[1]);
        this.queryParams = parseQueryParams(requestLineParts[1]);
        this.protocol = requestLineParts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(final String queryParamName) {
        return queryParams.getOrDefault(queryParamName, "");
    }

    public String getProtocol() {
        return protocol;
    }

    private String[] validateAndParseRequestLine(final String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Request line 값이 null 또는 비어있습니다.");
        }
        final String[] parts = requestLine.trim().split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Request line 형식이 올바르지 않습니다.");
        }
        return parts;
    }

    private String parsePath(final String pathWithQuery) {
        final int queryIndex = pathWithQuery.indexOf("?");
        // 문자열 크기 내 '?' 가 있다면 '?' 이전 문자열을 반환한다.
        if (0 <= queryIndex && queryIndex < pathWithQuery.length()) {
            return pathWithQuery.substring(0, queryIndex);
        }
        // ?가 없다면 전체 문자열을 반환한다.
        return pathWithQuery;
    }

    private Map<String, String> parseQueryParams(final String pathWithQuery) {
        final int queryIndex = pathWithQuery.indexOf("?");
        // 문자열 크기 내 '?'가 없다면 빈 Map을 반환한다.
        if (queryIndex < 0 || pathWithQuery.length() <= queryIndex) {
            return Map.of();
        }
        // '?' 이후 문자열에서 쿼리 파라미터를 파싱한다.
        final String queryString = pathWithQuery.substring(queryIndex + 1);
        final String[] params = queryString.split("&");
        final Map<String, String> queryParams = new HashMap<>();
        java.util.Arrays.stream(params)
                .map(param -> param.split("="))
                .filter(pairs -> pairs.length == 2)
                .forEach(pairs -> queryParams.put(pairs[0], pairs[1]));
        return queryParams;
    }
}

package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestLine {

    private static final String WHITESPACE_REGEX = " ";
    private static final String QUESTION_MARK = "?";
    private static final String QUERY_DELIMITER = "&";
    private static final int REQUEST_LINE_PARTS = 3;

    private final String method;
    private final String path;
    private final Map<String, String> queryParameters;
    private final String version;

    private RequestLine(String method, String path, Map<String, String> queryParameters,
        String version) {
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
        this.version = version;
    }

    public static RequestLine from(String requestLine) {
        String[] parts = requestLine.strip().split(WHITESPACE_REGEX);
        if (parts.length != REQUEST_LINE_PARTS) {
            throw new IllegalArgumentException("잘못된 요청 라인입니다: " + requestLine);
        }

        String uri = parts[1];
        int index = uri.indexOf(QUESTION_MARK);
        if (index == -1) {
            return new RequestLine(parts[0], uri, Map.of(), parts[2]);
        }

        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        return new RequestLine(parts[0], path,
            KeyValueParser.parse(queryString, QUERY_DELIMITER), parts[2]);
    }

    public boolean hasQueryString() {
        return !queryParameters.isEmpty();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(String name) {
        return queryParameters.get(name);
    }

    public String getVersion() {
        return version;
    }
}

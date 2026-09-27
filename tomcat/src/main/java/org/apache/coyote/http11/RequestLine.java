package org.apache.coyote.http11;

public class RequestLine {

    private static final String SEPARATOR = " ";
    private static final String QUERY_SEPARATOR = "?";
    private static final int REQUEST_LINE_TOKEN_COUNT = 3;

    private final HttpMethod method;
    private final String path;
    private final String protocol;

    private RequestLine(final HttpMethod method, final String path, final String protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public static RequestLine from(final String requestLine) {
        final String[] tokens = requestLine.trim().split(SEPARATOR);
        if (tokens.length != REQUEST_LINE_TOKEN_COUNT) {
            throw new IllegalArgumentException("잘못된 요청 라인입니다. requestLine: " + requestLine);
        }
        return new RequestLine(HttpMethod.from(tokens[0]), extractPath(tokens[1]), tokens[2]);
    }

    private static String extractPath(final String requestTarget) {
        final int queryIndex = requestTarget.indexOf(QUERY_SEPARATOR);
        if (queryIndex == -1) {
            return requestTarget;
        }
        return requestTarget.substring(0, queryIndex);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }
}

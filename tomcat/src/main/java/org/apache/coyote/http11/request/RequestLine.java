package org.apache.coyote.http11.request;

public class RequestLine {

    private final String method;
    private final String path;
    private final String queryString;

    public RequestLine(final String requestLine) {
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("요청을 읽을 수 없음");
        }
        final String[] parts = requestLine.strip().split(" ", -1);
        final int queryIndex = parts[1].indexOf("?");
        this.method = parts[0];
        this.path = extractPath(queryIndex, parts[1]);
        this.queryString = extractQueryString(queryIndex, parts[1]);
    }

    private static String extractPath(int queryIndex, String uri) {
        if (queryIndex == -1) {
            return uri;
        }
        return uri.substring(0, queryIndex);
    }

    private static String extractQueryString(int queryIndex, String uri) {
        if (queryIndex == -1) {
            return "";
        }
        return uri.substring(queryIndex + 1);
    }

    public String method() {
        return method;
    }

    public String path() {
        return path;
    }

    public String queryString() {
        return queryString;
    }
}

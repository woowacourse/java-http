package org.apache.coyote.http11;

public class RequestLine {
    private final String method;
    private final String path;
    private final String queryString;
    private final String protocolVersion;

    private RequestLine(final String method, final String path, final String queryString,
                        final String protocolVersion) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine from(final String requestLine) {
        validateNotNull(requestLine);
        final String[] parts = requestLine.split(" ");
        validatePartsCount(parts);
        final String uri = parts[1];
        return new RequestLine(parts[0], extractPath(uri), extractQueryString(uri), parts[2]);
    }

    private static void validateNotNull(final String requestLine) {
        if (requestLine == null) {
            throw new IllegalArgumentException("[ERROR] 요청 라인이 존재하지 않습니다.");
        }
    }

    private static void validatePartsCount(final String[] parts) {
        if (parts.length != 3) {
            throw new IllegalArgumentException("[ERROR] 요청 라인은 메서드, URI, 프로토콜 버전으로 구성되어야 합니다.");
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    private static String extractPath(final String uri) {
        final int queryStartIndex = uri.indexOf('?');
        if (queryStartIndex == -1) { // 쿼리가 없는 경우
            return uri;
        }
        return uri.substring(0, queryStartIndex);
    }

    private static String extractQueryString(final String uri) {
        final int queryStartIndex = uri.indexOf('?');
        if (queryStartIndex == -1) { // 쿼리가 없는 경우
            return "";
        }
        return uri.substring(queryStartIndex + 1);
    }
}

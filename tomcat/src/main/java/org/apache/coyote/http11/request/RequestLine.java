package org.apache.coyote.http11.request;

import java.net.URI;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final String queryString;

    private RequestLine(final HttpMethod method, final String path, final String version, final String queryString) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.queryString = queryString;
    }

    public static RequestLine from(final String requestLine) {
        final String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new HttpRequestParseException("요청 라인 형식이 잘못되었습니다: " + requestLine);
        }
        final URI uri = convertToURI(tokens[1]);
        return new RequestLine(toMethod(tokens[0]), extractPath(uri), tokens[2], extractQuery(uri));
    }

    private static HttpMethod toMethod(final String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new HttpRequestParseException("메서드 형식이 잘못되었습니다: " + method);
        }
    }

    private static URI convertToURI(final String uri) {
        try {
            return URI.create(uri);
        } catch (IllegalArgumentException e) {
            throw new HttpRequestParseException("Request Target 형식이 잘못되었습니다: " + uri);
        }
    }

    private static String extractPath(final URI uri) {
        final String path = uri.getPath();
        if (path == null) {
            throw new HttpRequestParseException("Path 형식이 잘못되었습니다: " + uri);
        }
        if (path.isEmpty()) {
            return "/";
        }
        return path;
    }

    private static String extractQuery(final URI uri) {
        final String rawQuery = uri.getRawQuery();
        if (rawQuery == null) {
            return "";
        }
        return rawQuery;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public String getQueryString() {
        return queryString;
    }
}

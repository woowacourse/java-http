package org.apache.coyote.http;

import java.net.URI;
import java.net.URISyntaxException;

public class RequestLine {

    private static final int PART_COUNT = 3;

    private HttpMethod httpMethod;
    private URI uri;
    private HttpVersion version;
    private QueryParam queryParam;

    private RequestLine(HttpMethod httpMethod, URI uri, HttpVersion version) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.version = version;
        this.queryParam = QueryParam.from(uri.getQuery());
    }

    public static RequestLine from(final String firstLine) {
        final String[] parts = splitLines(firstLine);

        return new RequestLine(
                parseMethod(parts[0]),
                parseUri(parts[1]),
                parseVersion(parts[2])
        );
    }

    private static HttpMethod parseMethod(String method) {
        return HttpMethod.from(method);
    }

    private static URI parseUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("잘못된 형식의 uri 입니다: " + uri);
        }
    }

    private static HttpVersion parseVersion(String version) {
        return HttpVersion.from(version);
    }

    private static String[] splitLines(String firstLine) {
        if (firstLine == null || firstLine.isBlank()) {
            throw new IllegalArgumentException("요청 라인이 비어 있습니다.");
        }

        final String[] parts = firstLine.trim().split(" ");
        if (parts.length != RequestLine.PART_COUNT) {
            throw new IllegalArgumentException("잘못된 요청 라인입니다: " + firstLine);
        }
        return parts;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }
}

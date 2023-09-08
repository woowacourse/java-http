package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public class HttpRequestLine {

    private static final String SEPARATOR = " ";

    private final HttpMethod httpMethod;
    private final Uri uri;
    private final HttpVersion httpVersion;

    public static HttpRequestLine from(final String requestLine) {
        final List<String> requestLineInfos = Arrays.asList(requestLine.split(SEPARATOR));
        return new HttpRequestLine(requestLineInfos.get(0), requestLineInfos.get(1), requestLineInfos.get(2));
    }

    public HttpRequestLine(
            final String httpMethod,
            final String uri,
            final String httpVersion
    ) {
        this.httpMethod = HttpMethod.valueOf(httpMethod);
        this.uri = Uri.from(uri);
        this.httpVersion = HttpVersion.valueOf(httpVersion);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Uri getUri() {
        return uri;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}

package org.apache.coyote.http11.request.model;

public class HttpRequestStartLine {

    private final HttpMethod method;
    private final HttpRequestUri uri;
    private final HttpVersion version;

    public HttpRequestStartLine(final HttpMethod method, final HttpRequestUri uri, final HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public HttpRequestStartLine(final String method, final String uri, final String version) {
        this(HttpMethod.of(method), HttpRequestUri.of(uri), HttpVersion.of(version));
    }

    public boolean isEqualToMethod(final HttpMethod httpMethod) {
        return method == httpMethod;
    }

    public boolean isEqualToUri(final String uri) {
        return this.uri.equals(HttpRequestUri.of(uri));
    }

    public boolean isQueryString() {
        return uri.isQuery();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpRequestUri getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }
}

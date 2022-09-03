package org.apache.coyote.http11.request.model;

public class HttpRequest {

    private final HttpMethod method;
    private final HttpRequestUri uri;
    private final HttpVersion version;

    public HttpRequest(final HttpMethod method, final HttpRequestUri uri, final HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public HttpRequest(final String method, final String uri, final String version) {
        this(HttpMethod.of(method), HttpRequestUri.of(uri), HttpVersion.of(version));
    }

    public boolean isGetMethod() {
        return method.isGet();
    }

    public boolean isIndex() {
        return uri.isIndex();
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

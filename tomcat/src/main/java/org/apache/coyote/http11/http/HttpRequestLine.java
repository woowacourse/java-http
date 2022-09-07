package org.apache.coyote.http11.http;

public class HttpRequestLine {

    private final HttpMethod method;
    private final HttpPath path;
    private final HttpVersion version;

    public HttpRequestLine(final HttpMethod method, final HttpPath path, final HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public HttpRequestLine(final String method, final String path, final String version) {
        this(HttpMethod.of(method), HttpPath.of(path), HttpVersion.of(version));
    }

    public boolean isEqualToMethod(final HttpMethod httpMethod) {
        return method == httpMethod;
    }

    public boolean isEqualToUri(final String uri) {
        return this.path.equals(HttpPath.of(uri));
    }

    public boolean isQueryString() {
        return path.isQuery();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpPath getPath() {
        return path;
    }

    public HttpVersion getVersion() {
        return version;
    }
}

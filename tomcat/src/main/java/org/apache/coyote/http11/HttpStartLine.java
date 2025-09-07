package org.apache.coyote.http11;

public class HttpStartLine {
    private final HttpMethod httpMethod;
    private final HttpUri uri;
    private final HttpProtocol protocol;

    public HttpStartLine(HttpMethod httpMethod, HttpUri uri, HttpProtocol protocol) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.protocol = protocol;
    }

    public HttpUri getUri() {
        return uri;
    }
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public String getResourcePath() {
        return uri.getPath();
    }
}

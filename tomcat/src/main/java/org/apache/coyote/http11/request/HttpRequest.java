package org.apache.coyote.http11.request;

import org.apache.coyote.http11.ContentType;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final HttpPath httpPath;
    private final HttpVersion httpVersion;
    private final HttpHeaders httpHeaders;

    public HttpRequest(final HttpRequestLine httpRequestLine, final HttpHeaders httpHeaders) {
        this.httpMethod = httpRequestLine.getMethod();
        this.httpPath = httpRequestLine.getPath();
        this.httpVersion = httpRequestLine.getVersion();
        this.httpHeaders = httpHeaders;
    }

    public boolean isLoginRequest() {
        return httpPath.isLoginRequest();
    }

    public boolean isDefaultRequest() {
        return httpPath.isDefaultRequest();
    }

    public boolean hasQueryParams() {
        return httpPath.hasQueryParams();
    }

    public String getParam(final String name) {
        return httpPath.getParam(name);
    }

    public ContentType getContentType() {
        return ContentType.from(httpPath.getPath());
    }

    public HttpPath getHttpPath() {
        return httpPath;
    }
}

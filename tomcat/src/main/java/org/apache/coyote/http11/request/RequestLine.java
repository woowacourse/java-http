package org.apache.coyote.http11.request;

public class RequestLine {
    private final HttpMethod httpMethod;
    private final RequestPath requestPath;
    private final RequestVersion requestVersion;

    public RequestLine(final HttpMethod httpMethod, final RequestPath requestPath,
                       final RequestVersion requestVersion) {
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.requestVersion = requestVersion;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestPath getRequestPath() {
        return requestPath;
    }

    public RequestVersion getRequestVersion() {
        return requestVersion;
    }
}

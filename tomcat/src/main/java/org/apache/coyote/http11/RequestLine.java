package org.apache.coyote.http11;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final RequestURI requestURI;
    private final HttpVersion httpVersion;

    public RequestLine(final String httpMethod, final String requestURI, final String httpVersion) {
        this(HttpMethod.valueOf(httpMethod), requestURI, HttpVersion.from(httpVersion));
    }

    public RequestLine(final HttpMethod httpMethod, final String requestURI, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestURI = new RequestURI(requestURI);
        this.httpVersion = httpVersion;
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestURI getRequestURI() {
        return requestURI;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}

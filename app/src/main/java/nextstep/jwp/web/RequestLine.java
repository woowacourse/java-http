package nextstep.jwp.web;

import java.net.URI;

public class RequestLine {
    private final HttpMethod method;
    private final URI requestUri;
    private final HttpVersion httpVersion;

    public RequestLine(HttpMethod method, URI requestUri, HttpVersion httpVersion) {
        this.method = method;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getRequestUri() {
        return requestUri;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}

package org.apache.coyote.http11.request;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpHeaders httpHeaders;

    public HttpRequest(final HttpRequestLine httpRequestLine, final HttpHeaders httpHeaders) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
    }
}

package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeaders;

public final class HttpRequest {

    private final RequestLine requestLine;
    private final RequestParams params;
    private final HttpHeaders headers;
    private final byte[] body;
    private final HttpCookie cookie;

    public HttpRequest(final RequestLine requestLine, final RequestParams params,
                       final HttpHeaders headers, final byte[] body) {
        this.requestLine = requestLine;
        this.params = params;
        this.headers = headers;
        this.body = body.clone();
        this.cookie = new HttpCookie(headers.get("cookie"));
    }

    public HttpHeaders headers() {
        return headers;
    }

    public byte[] body() {
        return body.clone();
    }

    public HttpCookie cookie() {
        return cookie;
    }

    public String path() {
        return requestLine.path();
    }

    public boolean isPost() {
        return "POST".equals(requestLine.method());
    }
}

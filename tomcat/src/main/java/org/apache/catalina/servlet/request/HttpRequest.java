package org.apache.catalina.servlet.request;

import org.apache.catalina.servlet.session.Cookies;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final Cookies cookies;
    private final Body body;

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, Body body) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.body = body;
        this.cookies = extractCookie(requestHeaders);
    }

    private Cookies extractCookie(RequestHeaders requestHeaders) {
        if (requestHeaders == null) {
            return new Cookies();
        }
        String cookies = requestHeaders.get("Cookie");
        if (cookies == null) {
            return new Cookies();
        }
        return Cookies.from(cookies);
    }

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public RequestHeaders headers() {
        return requestHeaders;
    }

    public String body() {
        return body.body();
    }

    public Cookies cookies() {
        return cookies;
    }

    public static class HttpRequestBuilder {

        private RequestLine requestLine;
        private RequestHeaders requestHeaders;
        private Body body;

        public HttpRequestBuilder startLine(RequestLine requestLine) {
            this.requestLine = requestLine;
            return this;
        }

        public HttpRequestBuilder headers(RequestHeaders requestHeaders) {
            this.requestHeaders = requestHeaders;
            return this;
        }

        public HttpRequestBuilder body(Body body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(requestLine, requestHeaders, body);
        }
    }
}

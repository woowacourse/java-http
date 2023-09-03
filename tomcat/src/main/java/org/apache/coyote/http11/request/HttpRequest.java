package org.apache.coyote.http11.request;

public class HttpRequest {

    private final StartLine startLine;
    private final RequestHeaders requestHeaders;
    private final Body body;

    private HttpRequest(StartLine startLine, RequestHeaders requestHeaders, Body body) {
        this.startLine = startLine;
        this.requestHeaders = requestHeaders;
        this.body = body;
    }

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    public StartLine startLine() {
        return startLine;
    }

    public RequestHeaders headers() {
        return requestHeaders;
    }

    public String body() {
        return body.body();
    }

    public static class HttpRequestBuilder {

        private StartLine startLine;
        private RequestHeaders requestHeaders;
        private Body body;

        public HttpRequestBuilder startLine(StartLine startLine) {
            this.startLine = startLine;
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
            return new HttpRequest(startLine, requestHeaders, body);
        }
    }
}

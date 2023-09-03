package org.apache.coyote.http11.request;

public class HttpRequest {

    private final StartLine startLine;
    private final Headers headers;
    private final Body body;

    private HttpRequest(StartLine startLine, Headers headers, Body body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    public StartLine startLine() {
        return startLine;
    }

    public Headers headers() {
        return headers;
    }

    public Body body() {
        return body;
    }

    public static class HttpRequestBuilder {

        private StartLine startLine;
        private Headers headers;
        private Body body;

        public HttpRequestBuilder startLine(StartLine startLine) {
            this.startLine = startLine;
            return this;
        }

        public HttpRequestBuilder headers(Headers headers) {
            this.headers = headers;
            return this;
        }

        public HttpRequestBuilder body(Body body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(startLine, headers, body);
        }
    }
}

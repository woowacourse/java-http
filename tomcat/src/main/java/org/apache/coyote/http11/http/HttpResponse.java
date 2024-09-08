package org.apache.coyote.http11.http;

import java.util.StringJoiner;

public class HttpResponse {

    private final String httpVersion;
    private final HttpStatusCode httpStatusCode;
    private final Headers headers;
    private final String body;

    private HttpResponse(String httpVersion, HttpStatusCode httpStatusCode, Headers headers, String body) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.headers = headers;
        this.body = body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        final var result = new StringJoiner("\r\n");
        final var statusLine = httpVersion + " " + httpStatusCode + " ";
        result.add(statusLine)
                .add(headers.toString())
                .add(body);
        return result.toString();
    }

    public static class Builder {

        private String httpVersion;
        private HttpStatusCode httpStatusCode;
        private Headers headers;
        private String body;

        public Builder mutate(Builder builder) {
            httpVersion = builder.httpVersion;
            httpStatusCode = builder.httpStatusCode;
            headers = builder.headers;
            body = builder.body;
            return this;
        }

        public Builder httpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder httpStatusCode(HttpStatusCode httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            return this;
        }

        public Builder headers(Headers headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(httpVersion, httpStatusCode, headers, body);
        }
    }
}

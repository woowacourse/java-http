package com.techcourse.presentation;

import org.apache.coyote.http11.Headers;

public record HttpResponse(
        String protocol,
        String statusCode,
        Headers headers,
        String body
) {
    public static Builder builder() {
        return new Builder();
    }

    public String toMessage() {
        return String.join("\r\n",
                protocol + " " + statusCode + " ",
                headers.toHeaderString(),
                body);
    }

    public static class Builder {
        private String protocol = "HTTP/1.1";
        private String statusCode = "200 OK";
        private Headers headers = new Headers();
        private String body = "";

        public Builder protocol(final String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder statusCode(final String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder header(final String name, final String value) {
            headers.add(name, value);
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public Builder found() {
            this.statusCode = "302 Found";
            return this;
        }

        public Builder badRequest() {
            this.statusCode = "400 Bad Request";
            return this;
        }

        public Builder seeOther() {
            this.statusCode = "303 See Other";
            return this;
        }

        public Builder contentType(final String contentType) {
            return header("Content-Type", contentType);
        }

        public Builder contentLength(final int length) {
            return header("Content-Length", String.valueOf(length));
        }

        public Builder addCookie(final String cookieValue) {
            return header("Set-Cookie", cookieValue);
        }

        public Builder location(final String uri) {
            return header("Location", uri);
        }

        public HttpResponse build() {
            return new HttpResponse(protocol, statusCode, headers, body);
        }
    }
}

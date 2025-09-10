package com.techcourse.presentation;

import java.util.LinkedHashMap;
import java.util.Map;

public record HttpResponse(
        String protocol,
        String statusCode,
        Map<String, String> headers,
        String body
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String protocol = "HTTP/1.1";
        private String statusCode = "200 OK";
        private Map<String, String> headers = new LinkedHashMap<>();
        private String body = "";

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder statusCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder body(String body) {
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

        public Builder contentType(String contentType) {
            this.headers.put("Content-Type", contentType);
            return this;
        }

        public Builder contentLength(int length) {
            this.headers.put("Content-Length", String.valueOf(length));
            return this;
        }

        public Builder addCookie(String cookieValue) {
            this.headers.put("Set-Cookie", cookieValue);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(protocol, statusCode, new LinkedHashMap<>(headers), body);
        }
    }
}

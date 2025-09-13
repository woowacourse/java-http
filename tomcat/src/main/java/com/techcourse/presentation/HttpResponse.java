package com.techcourse.presentation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public record HttpResponse(
        String protocol,
        String statusCode,
        LinkedHashMap<String, List<String>> headers,
        String body
) {
    public static Builder builder() {
        return new Builder();
    }

    public String toMessage() {
        final StringBuilder sb = new StringBuilder();

        for (Entry<String, List<String>> header : headers.sequencedEntrySet()) {
            sb.append(header.getKey()).append(": ").append(String.join("; ", header.getValue())).append(" \r\n");
        }

        return String.join("\r\n",
                protocol + " " + statusCode + " ",
                sb.toString(),
                body);
    }

    public static class Builder {
        private String protocol = "HTTP/1.1";
        private String statusCode = "200 OK";
        private LinkedHashMap<String, List<String>> headers = new LinkedHashMap<>();
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
            headers.compute(name, (k, v) -> {
                if (v == null) {
                    return List.of(value);
                }
                v.add(value);
                return v;
            });
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
            return new HttpResponse(protocol, statusCode, new LinkedHashMap<>(headers), body);
        }
    }
}

package com.java.http;

import java.nio.charset.StandardCharsets;
import java.util.*;

public record HttpResponse(
        String version,
        StatusCode statusCode,
        Map<String, String> headers,
        byte[] responseBody
) {
    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder(200);
    }

    public static class HttpResponseBuilder {

        private final String version = "HTTP/1.1";
        private final StatusCode statusCode;
        private final Map<String, String> headers = new HashMap<>();

        private byte[] responseBody;

        public HttpResponseBuilder(int statusCode) {
            this.statusCode = StatusCode.parse(statusCode);
        }

        public HttpResponseBuilder html(String data) {
            this.headers.put("Content-Type", "text/html;charset=utf-8");
            this.headers.put("Content-Length", String.valueOf(data.getBytes(StandardCharsets.UTF_8).length));
            this.responseBody = data.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        public HttpResponseBuilder html(byte[] data) {
            this.headers.put("Content-Type", "text/html;charset=utf-8");
            this.headers.put("Content-Length", String.valueOf(data.length));
            this.responseBody = data;
            return this;
        }

        public HttpResponseBuilder css(byte[] data) {
            this.headers.put("Content-Type", "text/css;charset=utf-8");
            this.headers.put("Content-Length", String.valueOf(data.length));
            this.responseBody = data;
            return this;
        }

        public HttpResponseBuilder js(byte[] data) {
            this.headers.put("Content-Type", "application/javascript;charset=utf-8");
            this.headers.put("Content-Length", String.valueOf(data.length));
            this.responseBody = data;
            return this;
        }

        public HttpResponseBuilder icon(byte[] data) {
            this.headers.put("Content-Type", "image/x-icon");
            this.headers.put("Content-Length", String.valueOf(data.length));
            this.responseBody = data;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(version, statusCode, Collections.unmodifiableMap(headers), responseBody);
        }
    }

    private enum StatusCode {
        OK(200),
        ;

        final int codeNumber;

        StatusCode(int codeNumber) {
            this.codeNumber = codeNumber;
        }

        public static StatusCode parse(int codeNumber) {
            return Arrays.stream(StatusCode.values())
                    .filter(value -> value.codeNumber == codeNumber)
                    .findFirst()
                    .orElseThrow();
            // TODO : 명확한 예외 타입 사용
        }
    }

    public byte[] toByteArray() {
        return toSimpleString().getBytes(StandardCharsets.UTF_8);
    }

    public String toSimpleString() {
        StringJoiner result = new StringJoiner("\r\n");

        result.add("%s %s %s ".formatted(version, statusCode.codeNumber, statusCode.name()));
        headers.forEach((key, value) -> result.add("%s: %s ".formatted(key, value)));
        result.add("");
        result.add(new String(responseBody, StandardCharsets.UTF_8));

        return result.toString();
    }

    @Override
    public String toString() {
        return toSimpleString();
    }
}

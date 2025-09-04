package com.java.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record HttpResponse(
        String version,
        StatusCode statusCode,
        Map<String, String> headers,
        byte[] responseBody
) {
    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder(200);
    }

    public static HttpResponse notFound(String message) {
        return new HttpResponseBuilder(404).plain(message).build();
    }

    public static HttpResponse internalServerError(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();
        return new HttpResponseBuilder(500).plain(stackTrace).build();
    }

    public static class HttpResponseBuilder {

        private final String version = "HTTP/1.1";
        private final StatusCode statusCode;
        private final Map<String, String> headers = new HashMap<>();

        private byte[] responseBody;

        public HttpResponseBuilder(int statusCode) {
            this.statusCode = StatusCode.parse(statusCode);
        }

        public HttpResponseBuilder plain(String data) {
            this.headers.put("Content-Type", "text/plain;charset=utf-8");
            this.headers.put("Content-Length", String.valueOf(data.getBytes(StandardCharsets.UTF_8).length));
            this.responseBody = data.getBytes(StandardCharsets.UTF_8);
            return this;
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
        OK("OK", 200),
        NOT_FOUND("Not Found", 404),
        INTERNAL_SERVER_ERROR("Internal Server Error", 500),
        ;

        final String codeName;
        final int codeNumber;

        StatusCode(String codeName, int codeNumber) {
            this.codeName = codeName;
            this.codeNumber = codeNumber;
        }

        public static StatusCode parse(int codeNumber) {
            return Arrays.stream(StatusCode.values())
                    .filter(value -> value.codeNumber == codeNumber)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태코드입니다. input=" + codeNumber));
        }
    }

    private static final String CRLF = "\r\n";

    public byte[] toByteArray() {
        StringBuilder sb = new StringBuilder();

        sb.append("%s %s %s".formatted(version, statusCode.codeNumber, statusCode.codeName)).append(CRLF);
        headers.forEach((key, value) -> sb.append("%s: %s".formatted(key, value)).append(CRLF));
        sb.append(CRLF);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            baos.write(responseBody);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("HTTP 응답을 구성하는 중에 예외가 발생했습니다.", e);
        }
    }
}

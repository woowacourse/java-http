package org.apache.coyote.http11.response;

import java.util.Map;

public class ResponseEntity {

    private static final String DEFAULT_CONTENT_TYPE = "text/plain;charset=utf-8";
    private final HttpResponse httpResponse;

    private ResponseEntity(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public static Builder ok() {
        return new Builder(DEFAULT_CONTENT_TYPE, null);
    }

    public static HttpResponse ok(Map<String, String> headers, byte[] body) {
        return new HttpResponse(headers, body);
    }

    public static HttpResponse ok(String body) {
        return buildResponse(body.getBytes(), DEFAULT_CONTENT_TYPE);
    }

    public static HttpResponse ok(byte[] body, String contentType) {
        return buildResponse(body, contentType);
    }

    private static HttpResponse buildResponse(byte[] body, String contentType) {
        if (body == null) {
            body = new byte[0];
        }
        Map<String, String> headers = Map.of(
                "Content-Type", contentType,
                "Content-Length", String.valueOf(body.length)
        );

        return new HttpResponse(headers, body);
    }

    public static class Builder {

        private final String contentType;
        private byte[] body;

        private Builder(String contentType, byte[] body) {
            this.contentType = contentType;
            this.body = body;
        }

        public HttpResponse build() {
            return buildResponse(body, contentType);
        }
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}

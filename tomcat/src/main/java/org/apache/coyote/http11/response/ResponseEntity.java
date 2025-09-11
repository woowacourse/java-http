package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseEntity {

    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_ENCODING_TYPE = "charset=utf-8";
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

    public static HttpResponse found(Map<String, String> headers) {
        return new HttpResponse(HttpStatus.FOUND, headers, null);
    }

    public static HttpResponse unauthorized(Map<String, String> headers) {
        return new HttpResponse(HttpStatus.UNAUTHORIZED, headers, null);
    }

    public static HttpResponse notFound() {
        return buildResponse(HttpStatus.NOT_FOUND, null, DEFAULT_CONTENT_TYPE);
    }

    public static HttpResponse noContent() {
        return buildResponse(HttpStatus.NO_CONTENT, null, DEFAULT_CONTENT_TYPE);
    }

    public static HttpResponse notAllowed() {
        return buildResponse(HttpStatus.NOT_ALLOWED, null, DEFAULT_CONTENT_TYPE);
    }

    private static HttpResponse buildResponse(byte[] body, String contentType) {
        return buildResponse(HttpStatus.OK, body, contentType);
    }

    private static HttpResponse buildResponse(HttpStatus httpStatus, byte[] body, String contentType) {
        return buildResponse(httpStatus, Map.of(), body, contentType);
    }

    private static HttpResponse buildResponse(HttpStatus httpStatus, Map<String, String> additionalHeaders, byte[] body, String contentType) {
        if (body == null) {
            body = new byte[0];
        }
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType + ";" + DEFAULT_ENCODING_TYPE);
        headers.put("Content-Length", String.valueOf(body.length));

        headers.putAll(additionalHeaders);

        return new HttpResponse(httpStatus, headers, body);
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

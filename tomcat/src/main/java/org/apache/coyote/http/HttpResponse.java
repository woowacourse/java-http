package org.apache.coyote.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final HttpStatus status;
    private final HttpHeaders headers;
    private final ResponseBody body;

    private HttpResponse(HttpStatus status, Map<String, String> headers, ResponseBody body) {
        this.status = status;
        this.headers = HttpHeaders.from(withContentHeaders(headers, body));
        this.body = body;
    }

    public static HttpResponse ok(ResponseBody body) {
        return of(HttpStatus.OK, body);
    }

    public static HttpResponse of(HttpStatus status, ResponseBody body) {
        return new HttpResponse(status, Map.of(), body);
    }

    public static HttpResponse redirect(String location) {
        return new HttpResponse(HttpStatus.FOUND, Map.of("Location", location), EmptyBody.INSTANCE);
    }

    private static Map<String, String> withContentHeaders(Map<String, String> headers, ResponseBody body) {
        final Map<String, String> values = new LinkedHashMap<>();
        final int contentLength = body.bytes().length;
        if (contentLength > 0) {
            values.put(CONTENT_TYPE, body.contentType());
        }
        values.put(CONTENT_LENGTH, String.valueOf(contentLength));
        values.putAll(headers);
        return values;
    }

    public HttpResponse addCookie(String key, String value) {
        headers.setCookie(key, value);
        return this;
    }

    public HttpStatus status() {
        return status;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public ResponseBody body() {
        return body;
    }
}

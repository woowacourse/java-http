package org.apache.coyote;

import java.util.Objects;
import java.util.Map;

public record HttpResponse(
        String statusCode,
        String contentType,
        byte[] body,
        Map<String, String> headers
) {

    public HttpResponse {
        Objects.requireNonNull(statusCode);
        Objects.requireNonNull(contentType);
        body = Objects.requireNonNull(body).clone();
        headers = Map.copyOf(headers);
    }

    public HttpResponse(String statusCode, String contentType, byte[] body) {
        this(statusCode, contentType, body, Map.of());
    }

    @Override
    public byte[] body() {
        return body.clone();
    }

    public int contentLength() {
        return body.length;
    }

    public static HttpResponse ok(String contentType, byte[] body) {
        return new HttpResponse("200 OK", contentType, body);
    }

    public static HttpResponse notFound(byte[] body) {
        return new HttpResponse("404 Not Found", "text/html;charset=utf-8", body);
    }

    public static HttpResponse badRequest(byte[] body) {
        return new HttpResponse("400 Bad Request", "text/html;charset=utf-8", body);
    }

    public static HttpResponse redirect(String location) {
        return new HttpResponse("302 Found", "text/html;charset=utf-8", new byte[0],
                Map.of("Location", location));
    }
}

package org.apache.coyote;

import java.util.Objects;

public record HttpResponse(
        String statusCode,
        String contentType,
        byte[] body
) {

    public HttpResponse {
        Objects.requireNonNull(statusCode);
        Objects.requireNonNull(contentType);
        body = Objects.requireNonNull(body).clone();
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
}

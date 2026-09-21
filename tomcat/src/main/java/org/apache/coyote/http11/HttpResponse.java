package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

final class HttpResponse {

    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

    private final String body;
    private final HttpStatus status;
    private final HttpHeaders headers;

    public HttpResponse(final HttpStatus status, final String contentType, final String body) {
        this.body = Objects.requireNonNull(body);
        this.status = Objects.requireNonNull(status);
        this.headers = new HttpHeaders();
        headers.add("Content-Type", Objects.requireNonNull(contentType));
        headers.add("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    HttpResponse addHeader(final String name, final String value) {
        headers.add(name, value);
        return this;
    }

    static HttpResponse ok(final String contentType, final String body) {
        return new HttpResponse(HttpStatus.OK, contentType, body);
    }

    static HttpResponse redirect(final String location) {
        return new HttpResponse(HttpStatus.FOUND, HTML_CONTENT_TYPE, " ")
                .addHeader("Location", location);
    }

    public byte[] toBytes() {
        final String response = "HTTP/1.1 " + status + " \r\n"
                + headers.toHeaderLines()
                + "\r\n\r\n"
                + body;
        return response.getBytes(StandardCharsets.UTF_8);
    }
}

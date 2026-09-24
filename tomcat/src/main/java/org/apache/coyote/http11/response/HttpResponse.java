package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.coyote.http11.header.HttpHeaders;

public final class HttpResponse {

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

    public static HttpResponse badRequest(String message) {
        return new HttpResponse(HttpStatus.BAD_REQUEST, "text/plain", message);
    }

    public static HttpResponse ok(final String contentType, final String body) {
        return new HttpResponse(HttpStatus.OK, contentType, body);
    }

    public static HttpResponse redirect(final String location) {
        return new HttpResponse(HttpStatus.FOUND, HTML_CONTENT_TYPE, "")
                .addHeader("Location", location);
    }

    public HttpResponse addHeader(final String name, final String value) {
        headers.add(name, value);
        return this;
    }

    public byte[] toBytes() {
        final String response = "HTTP/1.1 " + status + " \r\n"
                + headers.toHeaderLines()
                + "\r\n\r\n"
                + body;
        return response.getBytes(StandardCharsets.UTF_8);
    }
}

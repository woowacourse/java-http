package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;

public class Http11Response {

    private static final String CRLF = "\r\n";

    private final HttpStatus status;
    private final Http11ResponseHeaders headers;
    private final String body;

    private Http11Response(final HttpStatus status, final Http11ResponseHeaders headers, final String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static Http11Response ok(final String contentType, final String body) {
        Http11ResponseHeaders headers = Http11ResponseHeaders.create();
        headers.addHeader("Content-Type", contentType);
        headers.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        return new Http11Response(HttpStatus.OK, headers, body);
    }

    public static Http11Response notFound(final String contentType, final String body) {
        Http11ResponseHeaders headers = Http11ResponseHeaders.create();
        headers.addHeader("Content-Type", contentType);
        headers.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        return new Http11Response(HttpStatus.NOT_FOUND, headers, body);
    }

    public static Http11Response redirect(final String location) {
        Http11ResponseHeaders headers = Http11ResponseHeaders.create();
        headers.addHeader("Location", location);
        headers.addHeader("Content-Length", "0");
        return new Http11Response(HttpStatus.FOUND, headers, "");
    }

    public static Http11Response redirect(final String location, final String cookieHeader) {
        Http11ResponseHeaders headers = Http11ResponseHeaders.create();
        headers.addHeader("Location", location);
        headers.addHeader("Set-Cookie", cookieHeader);
        headers.addHeader("Content-Length", "0");
        return new Http11Response(HttpStatus.FOUND, headers, "");
    }

    public static Http11Response serverError() {
        String body = "Internal Server Error";
        Http11ResponseHeaders headers = Http11ResponseHeaders.create();
        headers.addHeader("Content-Type", "text/html;charset=utf-8");
        headers.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        return new Http11Response(HttpStatus.INTERNAL_SERVER_ERROR, headers, body);
    }

    public byte[] toBytes() {
        String response = "HTTP/1.1 " + status.getCode() + " " + status.getReason() + " " + CRLF
                + headers.toHeaderString() + CRLF + CRLF
                + body;

        return response.getBytes(StandardCharsets.UTF_8);
    }
}

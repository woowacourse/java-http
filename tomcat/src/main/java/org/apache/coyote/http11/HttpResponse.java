package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {
    private static final String VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private final HttpStatus status;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(final HttpStatus status, final Map<String, String> headers, final String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse ok(final String contentType, final String body) {
        return new HttpResponse(HttpStatus.OK, contentHeaders(contentType, body), body);
    }

    public static HttpResponse notFound(final String contentType, final String body) {
        return new HttpResponse(HttpStatus.NOT_FOUND, contentHeaders(contentType, body), body);
    }

    public static HttpResponse redirect(final String location) {
        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", location);
        headers.put("Content-Length", "0");
        return new HttpResponse(HttpStatus.FOUND, headers, "");
    }

    public byte[] getBytes() {
        final StringBuilder message = new StringBuilder(statusLine());
        headers.forEach((name, value) ->
                message.append(name).append(": ").append(value).append(" ").append(CRLF));
        message.append(CRLF).append(body);
        return message.toString().getBytes(UTF_8);
    }

    private static Map<String, String> contentHeaders(final String contentType, final String body) {
        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes(UTF_8).length));
        return headers;
    }

    private String statusLine() {
        return VERSION + " " + status.getCode() + " " + status.getReasonPhrase() + " " + CRLF;
    }
}

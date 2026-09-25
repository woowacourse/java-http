package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
    private static final String CRLF = "\r\n";
    private static final String VERSION = "HTTP/1.1 ";
    private static final String HEADER_DELIMITER = ": ";
    private static final String HEADER_SUFFIX = " ";

    private static final String STATUS_OK = "200 OK";
    private static final String STATUS_FOUND = "302 Found";
    private static final String STATUS_BAD_REQUEST = "400 Bad Request";
    private static final String STATUS_NOT_FOUND = "404 Not Found";

    private static final String LOCATION = "Location";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";

    private static final byte[] EMPTY_BODY = new byte[0];

    private final String status;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = EMPTY_BODY;

    private HttpResponse(final String status) {
        this.status = status;
    }

    public static HttpResponse ok(final ContentType contentType, final byte[] body) {
        return of(STATUS_OK, contentType, body);
    }

    public static HttpResponse notFound(final ContentType contentType, final byte[] body) {
        return of(STATUS_NOT_FOUND, contentType, body);
    }

    public static HttpResponse badRequest(final ContentType contentType, final byte[] body) {
        return of(STATUS_BAD_REQUEST, contentType, body);
    }

    public static HttpResponse error(final ContentType contentType, final byte[] body) {
        return of(STATUS_BAD_REQUEST, contentType, body);
    }

    public static HttpResponse redirect(final String location) {
        final HttpResponse response = new HttpResponse(STATUS_FOUND);
        response.headers.put(LOCATION, location);
        response.setBody(EMPTY_BODY);
        return response;
    }

    private static HttpResponse of(
            final String status,
            final ContentType contentType,
            final byte[] body
    ) {
        final HttpResponse response = new HttpResponse(status);
        response.headers.put(CONTENT_TYPE, contentType.getValue());
        response.setBody(body);
        return response;
    }

    public void addCookie(final String cookie) {
        this.headers.put(SET_COOKIE, cookie);
    }

    public void writeTo(final OutputStream outputStream) throws IOException {
        final StringBuilder builder = new StringBuilder();
        builder.append(VERSION).append(status).append(HEADER_SUFFIX).append(CRLF);

        for (final Map.Entry<String, String> header : headers.entrySet()) {
            builder.append(header.getKey())
                    .append(HEADER_DELIMITER)
                    .append(header.getValue())
                    .append(HEADER_SUFFIX)
                    .append(CRLF);
        }
        builder.append(CRLF);

        outputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private void setBody(final byte[] body) {
        this.body = body;
        this.headers.put(CONTENT_LENGTH, String.valueOf(body.length));
    }
}

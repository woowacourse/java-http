package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private StatusLine statusLine = new StatusLine(HttpStatus.OK);
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public void setStatus(final HttpStatus status) {
        this.statusLine = new StatusLine(status);
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void setBody(final String contentType, final byte[] body) {
        addHeader(CONTENT_TYPE, contentType);
        addHeader(CONTENT_LENGTH, String.valueOf(body.length));
        this.body = body;
    }

    public void sendRedirect(final String location) {
        setStatus(HttpStatus.FOUND);
        addHeader(LOCATION, location);
    }

    public void setCookie(final String cookie) {
        addHeader(SET_COOKIE, cookie);
    }

    public byte[] toBytes() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(formatHead().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        return outputStream.toByteArray();
    }

    private String formatHead() {
        final StringBuilder head = new StringBuilder(statusLine.format()).append(CRLF);
        headers.forEach((name, value) -> head.append(name).append(": ").append(value).append(" ").append(CRLF));
        return head.append(CRLF).toString();
    }
}

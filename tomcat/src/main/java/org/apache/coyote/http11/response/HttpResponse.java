package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {
    private static final String VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private HttpStatus status;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.status = HttpStatus.OK;
        this.headers = new LinkedHashMap<>();
        this.body = "";
    }

    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void setBody(final String contentType, final String body) {
        setHeader("Content-Type", contentType + ";charset=utf-8");
        this.body = body;
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public void sendRedirect(final String location) {
        setStatus(HttpStatus.FOUND);
        setHeader("Location", location);
        headers.remove("Content-Type");
        this.body = "";
    }

    public byte[] getBytes() {
        final StringBuilder message = new StringBuilder(statusLine());
        headers.forEach((name, value) -> appendHeader(message, name, value));
        appendHeader(message, "Content-Length", String.valueOf(body.getBytes(UTF_8).length));
        message.append(CRLF).append(body);
        return message.toString().getBytes(UTF_8);
    }

    private String statusLine() {
        return VERSION + " " + status.getCode() + " " + status.getReasonPhrase() + " " + CRLF;
    }

    private void appendHeader(final StringBuilder message, final String name, final String value) {
        message.append(name).append(": ").append(value).append(" ").append(CRLF);
    }
}

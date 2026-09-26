package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";
    private static final String CHARSET = ";charset=utf-8";

    private final Map<String, String> headers = new LinkedHashMap<>();
    private final List<String> cookies = new ArrayList<>();
    private HttpStatus status = HttpStatus.OK;
    private String body = "";

    public void ok(ContentType contentType, String body) {
        this.status = HttpStatus.OK;
        this.body = body;
        headers.put("Content-Type", contentType.getValue() + CHARSET);
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void redirect(String location) {
        this.status = HttpStatus.FOUND;
        headers.put("Location", location);
    }

    public void addCookie(String name, String value) {
        cookies.add(name + "=" + value);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public byte[] getBytes() {
        StringBuilder message = new StringBuilder();
        message.append(VERSION).append(" ")
                .append(status.getCode()).append(" ")
                .append(status.getReasonPhrase()).append(" ").append(CRLF);
        cookies.forEach(cookie -> appendHeader(message, "Set-Cookie", cookie));
        headers.forEach((name, value) -> appendHeader(message, name, value));
        message.append(CRLF).append(body);
        return message.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void appendHeader(StringBuilder message, String name, String value) {
        message.append(name).append(": ").append(value).append(" ").append(CRLF);
    }
}

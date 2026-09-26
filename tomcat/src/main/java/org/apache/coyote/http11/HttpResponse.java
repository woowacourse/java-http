package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final List<String> cookies = new ArrayList<>();
    private String body = "";

    public void setBody(String contentType, String body) {
        this.status = HttpStatus.OK;
        this.body = body;
        headers.put("Content-Type", contentType + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void sendRedirect(String location) {
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
        cookies.forEach(cookie -> message.append("Set-Cookie: ").append(cookie).append(" ").append(CRLF));
        headers.forEach((name, value) -> message.append(name).append(": ").append(value).append(" ").append(CRLF));
        message.append(CRLF).append(body);
        return message.toString().getBytes(StandardCharsets.UTF_8);
    }
}

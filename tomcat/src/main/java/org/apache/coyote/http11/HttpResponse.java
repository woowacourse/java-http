package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
    private StatusLine statusLine;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.statusLine = new StatusLine(HttpStatus.OK);
        this.headers = new LinkedHashMap<>();
        this.body = "";
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public String format() {
        final StringBuilder sb = new StringBuilder();
        sb.append(statusLine.format()).append("\r\n");
        for (final Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        sb.append("Content-Length: ").append(body.getBytes(StandardCharsets.UTF_8).length).append("\r\n");
        sb.append("\r\n"); // 헤더 끝날 때 빈 줄
        sb.append(body);
        return sb.toString();
    }

    public void ok(final String contentType, final String responseBody) {
        this.statusLine = new StatusLine(HttpStatus.OK);
        addHeader("Content-Type", contentType + ";charset=utf-8");
        this.body = responseBody;
    }

    public void redirect(final String location) {
        this.statusLine = new StatusLine(HttpStatus.FOUND);
        addHeader("Location", location);
    }

    public void redirectWithCookie(final String location, final String sessionId) {
        redirect(location);
        addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
    }
}

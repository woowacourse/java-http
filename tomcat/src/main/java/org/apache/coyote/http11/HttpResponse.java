package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
    private final StatusLine statusLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(final StatusLine statusLine, final String body) {
        this.statusLine = statusLine;
        this.headers = new LinkedHashMap<>();
        this.body = body;
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

    public static HttpResponse ok(final String contentType, final String responseBody) {
        final HttpResponse response = new HttpResponse(new StatusLine(HttpStatus.OK), responseBody);
        response.addHeader("Content-Type", contentType + ";charset=utf-8");
        return response;
    }

    public static HttpResponse redirect(final String location) {
        final HttpResponse response = new HttpResponse(new StatusLine(HttpStatus.FOUND), "");
        response.addHeader("Location", location);
        return response;
    }

    public static HttpResponse redirectWithCookie(final String location, final String sessionId) {
        final HttpResponse response = redirect(location);
        response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
        return response;
    }
}

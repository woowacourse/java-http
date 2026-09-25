package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private String statusLine;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body = "";

    public HttpResponse() {
    }

    public void ok(String contentType, String body) {
        this.statusLine = PROTOCOL_VERSION + " 200 OK";
        this.headers.put("Content-Type", contentType);
        this.body = body == null ? "" : body;
    }

    public void sendRedirect(String redirectPath) {
        this.statusLine = PROTOCOL_VERSION + " 302 Found";
        this.headers.put("Location", redirectPath);
        this.body = "";
    }

    public void notFound() {
        this.statusLine = PROTOCOL_VERSION + " 404 Not Found";
        this.headers.put("Content-Type", "text/plain;charset=utf-8");
        this.body = "Not Found";
    }

    public void methodNotAllowed() {
        this.statusLine = PROTOCOL_VERSION + " 405 Method Not Allowed";
        this.headers.put("Content-Type", "text/plain;charset=utf-8");
        this.body = "Method Not Allowed";
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String toResponse() {
        if (statusLine == null) {
            throw new IllegalStateException("HTTP 응답 상태가 설정되지 않았습니다.");
        }

        int contentLength = body.getBytes(StandardCharsets.UTF_8).length;
        headers.put("Content-Length", String.valueOf(contentLength));

        StringBuilder response = new StringBuilder();
        response.append(statusLine).append("\r\n");
        headers.forEach((key, value) ->
                response.append(key)
                        .append(": ")
                        .append(value)
                        .append("\r\n"));

        response.append("\r\n");
        response.append(body);
        return response.toString();
    }
}

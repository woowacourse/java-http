package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private String statusLine;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public HttpResponse() {
    }

    public void ok(String contentType, String body) {
        ok(contentType, body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8));
    }

    public void ok(String contentType, byte[] body) {
        this.statusLine = PROTOCOL_VERSION + " 200 OK";
        this.headers.put("Content-Type", contentType);
        this.body = body == null ? new byte[0] : Arrays.copyOf(body, body.length);
    }

    public void sendRedirect(String redirectPath) {
        this.statusLine = PROTOCOL_VERSION + " 302 Found";
        this.headers.put("Location", redirectPath);
        this.body = new byte[0];
    }

    public void notFound() {
        this.statusLine = PROTOCOL_VERSION + " 404 Not Found";
        this.headers.put("Content-Type", "text/plain;charset=utf-8");
        this.body = "Not Found".getBytes(StandardCharsets.UTF_8);
    }

    public void methodNotAllowed() {
        this.statusLine = PROTOCOL_VERSION + " 405 Method Not Allowed";
        this.headers.put("Content-Type", "text/plain;charset=utf-8");
        this.body = "Method Not Allowed".getBytes(StandardCharsets.UTF_8);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public byte[] toBytes() {
        if (statusLine == null) {
            throw new IllegalStateException("HTTP 응답 상태가 설정되지 않았습니다.");
        }

        headers.put("Content-Length", String.valueOf(body.length));

        StringBuilder head = new StringBuilder();
        head.append(statusLine).append("\r\n");
        headers.forEach((key, value) ->
                head.append(key)
                        .append(": ")
                        .append(value)
                        .append("\r\n"));

        head.append("\r\n");

        byte[] headBytes = head.toString().getBytes(StandardCharsets.UTF_8);
        byte[] responseBytes = Arrays.copyOf(headBytes, headBytes.length + body.length);
        System.arraycopy(body, 0, responseBytes, headBytes.length, body.length);
        return responseBytes;
    }
}

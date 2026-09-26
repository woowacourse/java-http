package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final Map<String, String> headers = new LinkedHashMap<>();
    private HttpStatus status = HttpStatus.OK;
    private byte[] body = new byte[0];

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeader(String name, String value) {
        if (name.contains("\r") || name.contains("\n") || value.contains("\r") || value.contains("\n")) {
            throw new IllegalArgumentException("헤더에는 줄바꿈을 포함할 수 없습니다.");
        }
        headers.put(name, value);
    }

    public void setContentType(String contentType) {
        setHeader("Content-Type", contentType);
    }

    public void setBody(String body) {
        setBody(body.getBytes(StandardCharsets.UTF_8));
    }

    public void setBody(byte[] body) {
        this.body = body;
        setHeader("Content-Length", Integer.toString(body.length));
    }

    public void sendRedirect(String location) {
        setStatus(HttpStatus.FOUND);
        setHeader("Location", location);
        setBody(new byte[0]);
    }

    public void sendError(HttpStatus status, String message) {
        setStatus(status);
        headers.remove("Location");
        setContentType("text/plain; charset=utf-8");
        setBody(message);
    }

    public void setCookie(String name, String value, String path) {
        setHeader("Set-Cookie", name + "=" + value + "; Path=" + path);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        setHeader("Content-Length", Integer.toString(body.length));

        String lineEnd = status == HttpStatus.OK ? " \r\n" : "\r\n";
        StringBuilder response = new StringBuilder("HTTP/1.1 ")
                .append(status.getCode()).append(' ').append(status.getReasonPhrase()).append(lineEnd);
        headers.forEach((name, value) -> response
                .append(name).append(": ").append(value)
                .append(name.equals("Content-Type") || name.equals("Content-Length") ? lineEnd : "\r\n"));
        response.append("\r\n");

        outputStream.write(response.toString().getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(body);
        outputStream.flush();
    }
}

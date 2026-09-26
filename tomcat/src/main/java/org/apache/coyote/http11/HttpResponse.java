package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private int statusCode = 200;
    private String reasonPhrase = "OK";
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public void setBody(final byte[] body, final String contentType) {
        this.body = body;
        headers.put("Content-Type", contentType + ";charset=utf-8");
    }

    public void sendRedirect(final String location) {
        statusCode = 302;
        reasonPhrase = "Found";
        headers.put("Location", location);
        body = new byte[0];
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void addCookie(final String name, final String value) {
        headers.put("Set-Cookie", name + "=" + value);
    }

    public void write(final OutputStream outputStream) throws IOException {
        headers.put("Content-Length", String.valueOf(body.length));
        final var response = new StringBuilder()
                .append("HTTP/1.1 ").append(statusCode).append(' ').append(reasonPhrase).append("\r\n");
        for (final Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        response.append("\r\n");
        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}

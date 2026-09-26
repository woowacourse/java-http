package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HttpResponse {

    private int statusCode = 200;
    private String reasonPhrase = "OK";
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public void status(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public void header(String name, String value) {
        if ("Content-Length".equalsIgnoreCase(name)) {
            throw new IllegalArgumentException("Content-Length는 본문에서 계산합니다.");
        }
        headers.keySet().removeIf(existing -> existing.equalsIgnoreCase(name));
        headers.put(name, value);
    }

    public void body(byte[] body) {
        this.body = body.clone();
    }

    public void redirect(String location) {
        status(302, "Found");
        header("Location", location);
        headers.keySet().removeIf(name -> name.equalsIgnoreCase("Content-Type"));
        body = new byte[0];
    }

    public void writeTo(OutputStream output) throws IOException {
        StringBuilder responseHeader = new StringBuilder()
                .append("HTTP/1.1 ").append(statusCode).append(' ').append(reasonPhrase)
                .append("\r\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseHeader.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }
        responseHeader.append("Content-Length: ").append(body.length).append("\r\n\r\n");

        output.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
        output.write(body);
        output.flush();
    }
}

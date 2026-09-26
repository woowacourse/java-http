package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String LINE_SEPARATOR = "\r\n";

    private final Map<String, String> headers = new LinkedHashMap<>();
    private int statusCode = 200;
    private String reasonPhrase = "OK";
    private byte[] body = new byte[0];

    private void setStatus(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setCookie(String name, String value) {
        setHeader("Set-Cookie", name + "=" + value);
    }

    public void setBody(byte[] body, String contentType) {
        this.body = body;
        setHeader("Content-Type", contentType);
    }

    public void notFound() {
        setStatus(404, "Not Found");
    }

    public void methodNotAllowed() {
        setStatus(405, "Method Not Allowed");
    }

    public void redirect(String location) {
        setStatus(302, "Found");
        setHeader("Location", location);
        headers.remove("Content-Type");
        body = new byte[0];
    }

    public byte[] toBytes() {
        setHeader("Content-Length", String.valueOf(body.length));

        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append(' ').append(reasonPhrase)
                .append(LINE_SEPARATOR);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue())
                    .append(LINE_SEPARATOR);
        }
        response.append(LINE_SEPARATOR);

        byte[] headerBytes = response.toString().getBytes(StandardCharsets.UTF_8);
        byte[] responseBytes = Arrays.copyOf(headerBytes, headerBytes.length + body.length);
        System.arraycopy(body, 0, responseBytes, headerBytes.length, body.length);

        return responseBytes;
    }
}

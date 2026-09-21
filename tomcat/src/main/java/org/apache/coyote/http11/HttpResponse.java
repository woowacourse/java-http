package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private HttpStatus status;
    private final Map<String, String> headers;
    private byte[] body;

    public HttpResponse() {
        this(HttpStatus.OK, Map.of(), "");
    }

    public HttpResponse(
            HttpStatus status,
            Map<String, String> headers,
            String body
    ) {
        this.status = Objects.requireNonNull(status);
        this.headers = new LinkedHashMap<>();
        Objects.requireNonNull(headers).forEach(this::setHeader);
        this.body = Objects.requireNonNull(body).getBytes(StandardCharsets.UTF_8);
    }

    public void setStatus(HttpStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    public void setHeader(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        if (name.equalsIgnoreCase("Content-Length")) {
            throw new IllegalArgumentException("Content-Length는 응답 본문으로부터 계산됩니다.");
        }

        headers.keySet().removeIf(name::equalsIgnoreCase);
        headers.put(name, value);
    }

    public void setContentType(String contentType) {
        setHeader("Content-Type", Objects.requireNonNull(contentType) + ";charset=utf-8");
    }

    public void setCookie(String name, String value) {
        setHeader("Set-Cookie", Objects.requireNonNull(name) + "=" + Objects.requireNonNull(value));
    }

    public void setBody(String body) {
        this.body = Objects.requireNonNull(body).getBytes(StandardCharsets.UTF_8);
    }

    public void sendRedirect(String location) {
        setStatus(HttpStatus.FOUND);
        setHeader("Location", Objects.requireNonNull(location));
        setBody("");
    }

    public void sendError(HttpStatus status, String message) {
        setStatus(status);
        setContentType("text/plain");
        setBody(message);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        Objects.requireNonNull(outputStream);

        outputStream.write(statusLine().getBytes(StandardCharsets.UTF_8));
        for (Map.Entry<String, String> header : headers.entrySet()) {
            outputStream.write(headerLine(header).getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(("Content-Length: " + body.length + "\r\n\r\n")
                .getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private String statusLine() {
        return HTTP_VERSION
                + " " + status.getCode()
                + " " + status.getMessage()
                + "\r\n";
    }

    private String headerLine(Map.Entry<String, String> header) {
        return header.getKey() + ": " + header.getValue() + "\r\n";
    }
}

package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private HttpStatus status;
    private final Map<String, String> headers;
    private byte[] body;

    public HttpResponse() {
        this.status = HttpStatus.OK;
        this.headers = new LinkedHashMap<>();
        this.body = new byte[0];
    }

    private HttpResponse(HttpStatus status, byte[] body) {
        this();
        this.status = status;
        this.body = body.clone();
    }

    public static HttpResponse ok(String body, String contentType) {
        return of(HttpStatus.OK, body.getBytes(StandardCharsets.UTF_8), contentType);
    }

    public static HttpResponse of(HttpStatus status, byte[] body, String contentType) {
        HttpResponse response = new HttpResponse(status, body);
        response.addHeader("Content-Type", contentType);
        return response;
    }

    public static HttpResponse redirect(String location) {
        HttpResponse response = new HttpResponse(HttpStatus.FOUND, new byte[0]);
        response.addHeader("Location", location);
        return response;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setContent(HttpStatus status, byte[] body, String contentType) {
        this.status = status;
        this.body = body.clone();
        headers.remove("Location");
        headers.put("Content-Type", contentType);
    }

    public void sendRedirect(String location) {
        this.status = HttpStatus.FOUND;
        this.body = new byte[0];
        headers.remove("Content-Type");
        headers.put("Location", location);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(createHead().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private String createHead() {
        StringBuilder head = new StringBuilder();
        appendStatusLine(head);
        appendHeaders(head);
        head.append("Content-Length: ").append(body.length).append(" \r\n");
        head.append("\r\n");
        return head.toString();
    }

    private void appendStatusLine(StringBuilder head) {
        head.append(HTTP_VERSION)
                .append(" ")
                .append(status.getCode())
                .append(" ")
                .append(status.getReasonPhrase())
                .append(" \r\n");
    }

    private void appendHeaders(StringBuilder head) {
        headers.forEach((name, value) -> head.append(name)
                .append(": ")
                .append(value)
                .append(" \r\n"));
    }
}

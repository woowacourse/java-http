package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.cookie.HttpCookie;

public final class HttpResponse {

    private final OutputStream outputStream;
    private String statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = Objects.requireNonNull(outputStream);
    }

    public HttpResponse setStatusCode(String statusCode) {
        this.statusCode = Objects.requireNonNull(statusCode);
        return this;
    }

    public HttpResponse addHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public HttpResponse setContentType(String contentType) {
        return this.addHeader("Content-Type", contentType);
    }

    public HttpResponse setLocation(String location) {
        return this.addHeader("Location", location);
    }

    public HttpResponse setCookie(HttpCookie cookie) {
        return this.addHeader("Set-Cookie", cookie.getHeaderValue());
    }

    public HttpResponse setBody(String body) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
        return this.addHeader("Content-Length", String.valueOf(this.body.length));
    }

    public HttpResponse setBody(byte[] body) {
        this.body = body;
        return this.addHeader("Content-Length", String.valueOf(this.body.length));
    }

    public void send() throws IOException {
        writeStatusLine();
        writeHeaders();
        writeBody();
        this.outputStream.flush();
    }

    private void writeStatusLine() throws IOException {
        String statusLine = String.format("HTTP/1.1 %s \r\n", this.statusCode);
        this.outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));
    }

    private void writeHeaders() throws IOException {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            String headerLine = String.format("%s: %s \r\n", header.getKey(), header.getValue());
            this.outputStream.write(headerLine.getBytes(StandardCharsets.UTF_8));
        }
        this.outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private void writeBody() throws IOException {
        if (body != null) {
            this.outputStream.write(body);
        }
    }
}
package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpResponse {

    private final HttpVersion version;
    private HttpStatus status;
    private final ResponseHeader responseHeader;
    private String body;

    public HttpResponse(HttpVersion version, HttpStatus status, ResponseHeader responseHeader, String body) {
        this.version = version;
        this.status = status;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public static HttpResponse defaultHttpResponse(HttpVersion version) {
        return new HttpResponse(version, HttpStatus.OK, new ResponseHeader(), "");
    }

    public HttpResponse ok() {
        status = HttpStatus.OK;

        return this;
    }

    public HttpResponse redirect(String location) {
        status = HttpStatus.REDIRECT;

        responseHeader.addHeader("Location", "http://localhost:8080/" + location);

        return this;
    }

    public HttpResponse addHeader(String name, String value) {
        responseHeader.addHeader(name, value);

        return this;
    }

    public void writeStaticResource(Path resourcePath) throws IOException {
        byte[] bytes = readAllBytes(resourcePath);

        String contentType = Files.probeContentType(resourcePath);
        if (contentType == null) {
            contentType = "application/octet-stream"; // fallback
        }

        responseHeader.addHeader("Content-Type", contentType + ";charset=utf-8");

        body = new String(bytes);
    }

    public void write(String value) throws IOException {
        body += value;

        responseHeader.addHeader("Content-Length", body.getBytes().length + ";charset=utf-8");
    }

    public String buildHttpResponse() {
        responseHeader.addHeader("Content-Length", Integer.toString(body.getBytes().length));

        StringBuilder sb = new StringBuilder();

        appendStartLine(sb);
        appendResponseHeader(sb);
        sb.append("\r\n");
        sb.append(body);

        return sb.toString();
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    private void appendStartLine(StringBuilder sb) {
        sb.append(version.getVersion())
                .append(" ")
                .append(status.getCode())
                .append(" ")
                .append(status.getMessage())
                .append("\r\n");
    }

    private void appendResponseHeader(StringBuilder sb) {
        responseHeader.getHeaders()
                .forEach((name, value) -> {
                    sb.append(name)
                            .append(": ")
                            .append(value)
                            .append("\r\n");
                });
    }

    private byte[] readAllBytes(final Path resourcePath) {
        try {
            return Files.readAllBytes(resourcePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

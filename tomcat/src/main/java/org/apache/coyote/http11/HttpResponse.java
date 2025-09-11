package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String CARRIAGE_RETURN_LINE_FEED = "\r\n";

    private StatusLine statusLine;
    private HttpHeaders headers;
    private byte[] body;

    private HttpResponse() {
        this.body = new byte[0];
    }

    private HttpResponse(final StatusLine statusLine, final HttpHeaders headers, final byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(final HttpStatus status, final HttpHeaders headers, final byte[] body) {
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine("HTTP/1.1", status));
        response.setHeaders(headers);
        response.setBody(body);
        return response;
    }

    public static HttpResponse ok(final String contentType, final byte[] body) {
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine("HTTP/1.1", HttpStatus.OK));
        response.setHeader("Content-Type", contentType);
        response.setBody(body);
        response.setHeader("Content-Length", String.valueOf(body.length));
        return response;
    }

    public static HttpResponse redirect(final String location) {
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine("HTTP/1.1", HttpStatus.FOUND));
        response.setHeader("Location", location);
        response.setHeader("Content-Length", "0");
        response.setBody(new byte[0]);
        return response;
    }

    public static HttpResponse error(final HttpStatus status, final byte[] body) {
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine("HTTP/1.1", status));
        response.setHeader("Content-Length", String.valueOf(body.length));
        response.setBody(body);
        return response;
    }

    public byte[] toBytes() {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final String line = statusLine.format() + CARRIAGE_RETURN_LINE_FEED;
            out.write(line.getBytes(StandardCharsets.UTF_8));

            for (final var header : headers.getHeaders().entrySet()) {
                for (final var value : header.getValue()) {
                    final String headerLine = header.getKey() + ": " + value + " " + CARRIAGE_RETURN_LINE_FEED;
                    out.write(headerLine.getBytes(StandardCharsets.UTF_8));
                }
            }

            out.write(CARRIAGE_RETURN_LINE_FEED.getBytes(StandardCharsets.UTF_8));
            out.write(body);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setHeaders(final HttpHeaders headers) {
        this.headers = headers;
    }

    public void setBody(final byte[] body) {
        this.body = body;
        setContentLength(body.length);
    }

    public void setHeader(final String name, final String value) {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
        }
        this.headers.setHeader(name, value);
    }

    public void setContentLength(int length) {
        headers.setContentLength(length);
    }
}

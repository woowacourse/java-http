package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";

    private final String httpVersion;
    private final HttpHeaders headers = new HttpHeaders();
    private int statusCode;
    private String reasonPhrase;
    private byte[] body = new byte[0];

    public HttpResponse(final String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void ok(final String contentType, final byte[] body) {
        setStatus(200, "OK ");
        putHeader("Content-Type", contentType + " ");
        putHeader("Content-Length", body.length + " ");
        setBody(body);
    }

    public void redirect(final String location, final String setCookie) {
        setStatus(302, "Found ");
        if (!setCookie.isEmpty()) {
            putHeader(SET_COOKIE_HEADER, setCookie + " ");
        }
        putHeader("Location", location + " ");
        putHeader("Content-Length", "0 ");
    }

    public void setStatus(final int statusCode, final String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public void putHeader(final String name, final String value) {
        headers.putHeader(name, value);
    }

    public void setBody(final byte[] body) {
        this.body = body.clone();
    }

    public void write(final OutputStream outputStream) throws IOException {
        writeStatusLine(outputStream);
        writeContentLength(outputStream);
        headers.writeTo(outputStream);
        outputStream.write(CRLF.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private void writeStatusLine(final OutputStream outputStream) throws IOException {
        final String statusLine = httpVersion + " " + statusCode + " " + reasonPhrase + CRLF;
        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));
    }

    private void writeContentLength(final OutputStream outputStream) throws IOException {
        final String contentLength = headers.getHeader("Content-Length");
        final String bodyLength = String.valueOf(body.length);
        if (contentLength == null) {
            final String header = "Content-Length: " + bodyLength + CRLF;
            outputStream.write(header.getBytes(StandardCharsets.UTF_8));
            return;
        }
        if (!contentLength.trim().equals(bodyLength)) {
            headers.putHeader("Content-Length", bodyLength);
        }
    }
}

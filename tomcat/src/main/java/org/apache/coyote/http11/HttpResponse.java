package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final String httpVersion;
    private final HttpHeaders headers = new HttpHeaders();
    private int statusCode;
    private String reasonPhrase;
    private byte[] body = new byte[0];

    public HttpResponse(final String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setStatus(final int statusCode, final String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public void setHeader(final String name, final String value) {
        headers.setHeader(name, value);
    }

    public void setBody(final byte[] body) {
        this.body = body.clone();
    }

    public void write(final OutputStream outputStream) throws IOException {
        writeStatusLine(outputStream);
        writeContentLength(outputStream);
        headers.writeTo(outputStream);
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private void writeStatusLine(final OutputStream outputStream) throws IOException {
        final String statusLine = httpVersion + " " + statusCode + " " + reasonPhrase + "\r\n";
        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));
    }

    private void writeContentLength(final OutputStream outputStream) throws IOException {
        final String contentLength = headers.getHeader("Content-Length");
        final String bodyLength = String.valueOf(body.length);
        if (contentLength == null) {
            final String header = "Content-Length: " + bodyLength + "\r\n";
            outputStream.write(header.getBytes(StandardCharsets.UTF_8));
            return;
        }
        if (!contentLength.trim().equals(bodyLength)) {
            headers.setHeader("Content-Length", bodyLength);
        }
    }
}

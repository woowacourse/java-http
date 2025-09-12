package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String CARRIAGE_RETURN_LINE_FEED = "\r\n";

    private StatusLine statusLine;
    private HttpHeaders headers;
    private byte[] body;
    private boolean committed = false;

    public HttpResponse() {
        this.headers = new HttpHeaders();
        this.body = new byte[0];
    }

    public HttpResponse(final HttpStatus status, final HttpHeaders headers, final byte[] body) {
        this.statusLine = new StatusLine("HTTP/1.1", status);
        this.headers = headers;
        this.body = body;
        this.committed = false;
    }

    public void ok(final byte[] body, final MimeType mimeType) {
        setStatusLine(new StatusLine("HTTP/1.1", HttpStatus.OK));
        setBody(body, mimeType);
    }

    public void redirect(final String location) {
        setStatusLine(new StatusLine("HTTP/1.1", HttpStatus.FOUND));
        setHeader("Location", location);
        setHeader("Content-Length", "0");
        setBody(new byte[0]);
    }

    public void redirect(final String location, final HttpCookie httpCookie) {
        setStatusLine(new StatusLine("HTTP/1.1", HttpStatus.FOUND));
        setHeader("Location", location);
        setHeader("Content-Length", "0");
        setCookies(httpCookie);
        setBody(new byte[0]);
    }

    public void error(final HttpStatus status, final byte[] body) {
        setStatusLine(new StatusLine("HTTP/1.1", status));
        setHeader("Content-Length", String.valueOf(body.length));
        setBody(body);
    }

    public void error(final HttpStatus status, final byte[] body, final MimeType mimeType) {
        setStatusLine(new StatusLine("HTTP/1.1", status));
        setHeader("Content-Length", String.valueOf(body.length));
        setBody(body, mimeType);
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
        if (committed) {
            return;
        }
        this.statusLine = statusLine;
    }

    public void setHeaders(final HttpHeaders headers) {
        if (committed) {
            return;
        }
        this.headers = headers;
    }

    public void setBody(final byte[] body) {
        if (committed) {
            return;
        }
        this.body = body;
        setContentLength(body.length);
    }

    public void setBody(final byte[] body, final MimeType mimeType) {
        if (committed) {
            return;
        }
        this.body = body;
        setContentType(mimeType);
        setContentLength(body.length);
    }

    public void setHeader(final String name, final String value) {
        if (committed) {
            return;
        }
        if (this.headers == null) {
            this.headers = new HttpHeaders();
        }
        this.headers.setHeader(name, value);
    }

    public void commit() {
        this.committed = true;
    }

    public void setCookies(final HttpCookie httpCookie) {
        if (committed) {
            return;
        }
        httpCookie.getAll().forEach((name, value) -> setHeader("Set-Cookie", name + "=" + value));
    }

    private void setContentLength(int length) {
        if (committed) {
            return;
        }
        headers.setContentLength(length);
    }

    private void setContentType(final MimeType contentType) {
        if (committed) {
            return;
        }
        headers.setContentType(contentType);
    }
}

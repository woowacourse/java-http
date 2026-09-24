package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private StatusLine statusLine;
    private final ResponseHeaders headers;
    private String body;
    private boolean committed;

    public HttpResponse() {
        this.statusLine = new StatusLine(
                HTTP_VERSION,
                200,
                "OK"
        );
        this.headers = new ResponseHeaders();
        this.body = "";
        this.committed = false;
    }

    public void setStatus(
            final int statusCode,
            final String reasonPhrase
    ) {
        this.statusLine = new StatusLine(
                HTTP_VERSION,
                statusCode,
                reasonPhrase
        );
        this.committed = true;
    }

    public void addHeader(final String name, final String value) {
        headers.addHeader(name, value);
    }

    public String getHeader(final String name) {
        return headers.getHeader(name);
    }

    public void setBody(final String body) {
        if (body == null) {
            this.body = "";
        } else {
            this.body = body;
        }

        this.committed = true;
    }

    public void sendRedirect(final String location) {
        setStatus(302, "Found");
        addHeader("Location", location);
        setBody("");
    }

    public boolean isCommitted() {
        return committed;
    }

    public String toResponse() {
        addContentLength();

        return statusLine.toResponseLine()
                + "\r\n"
                + headers.toResponseHeaders()
                + "\r\n"
                + body;
    }

    private void addContentLength() {
        final int contentLength = body
                .getBytes(StandardCharsets.UTF_8)
                .length;

        headers.setHeader(
                "Content-Length",
                String.valueOf(contentLength)
        );
    }
}
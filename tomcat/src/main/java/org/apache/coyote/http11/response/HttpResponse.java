package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private StatusLine statusLine;
    private final ResponseHeaders headers;
    private String body;
    private String resourcePath;

    public HttpResponse() {
        this.statusLine = new StatusLine(
                HTTP_VERSION,
                200,
                "OK"
        );
        this.headers = new ResponseHeaders();
        this.body = "";
    }

    public void setResourcePath(final String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }
    public boolean hasResourcePath() {
        return resourcePath != null;
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
    }

    public void sendRedirect(final String location) {
        setStatus(302, "Found");
        addHeader("Location", location);
        setBody("");
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
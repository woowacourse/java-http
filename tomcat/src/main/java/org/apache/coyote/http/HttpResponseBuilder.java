package org.apache.coyote.http;

public class HttpResponseBuilder {

    private HttpStatusLine statusLine;

    private final HttpHeaders headers;
    
    private HttpBody body;

    HttpResponseBuilder() {
        headers = new HttpHeaders();
    }

    public HttpResponseBuilder statusLine(final HttpStatusLine statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    public HttpResponseBuilder contentType(final String contentType) {
        headers.setContentType(contentType);
        return this;
    }

    public HttpResponseBuilder location(final String location) {
        headers.setLocation(location);
        return this;
    }

    public HttpResponseBuilder body(final HttpBody body) {
        this.body = body;
        headers.setContentLength(body.getLength());
        return this;
    }

    public HttpResponse build() {
        if (statusLine == null) {
            throw new IllegalStateException("StatusLine not set");
        }
        return new HttpResponse(statusLine, headers, body);
    }
}

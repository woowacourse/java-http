package org.apache.coyote.http;

public class HttpResponseBuilder {

    private HttpStatusLine statusLine;
    private HttpHeaders headers;
    private HttpBody body;

    HttpResponseBuilder() {
        headers = new HttpHeaders();
    }

    public HttpResponseBuilder mutate(final HttpResponseBuilder builder) {
        statusLine = builder.statusLine;
        headers = builder.headers;
        body = builder.body;
        return this;
    }

    public HttpResponseBuilder statusLine(final HttpStatusLine statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    public HttpResponseBuilder headers(final HttpHeaders httpHeaders) {
        this.headers = httpHeaders;
        return this;
    }

    public HttpResponseBuilder contentType(final String contentType) {
        headers.put("Content-Type", contentType);
        return this;
    }

    public HttpResponseBuilder body(final HttpBody body) {
        this.body = body;
        headers.put("Content-Length", String.valueOf(body.getLength()));
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(statusLine, headers, body);
    }
}

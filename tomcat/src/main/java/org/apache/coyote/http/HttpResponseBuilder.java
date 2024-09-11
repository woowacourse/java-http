package org.apache.coyote.http;

import static org.apache.coyote.http.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http.HttpHeaders.CONTENT_TYPE;

public class HttpResponseBuilder {

    private HttpStatusLine statusLine;
    private HttpHeaders headers;
    private HttpBody body;

    HttpResponseBuilder() {
        headers = new HttpHeaders();
    }

    public HttpResponseBuilder statusLine(final HttpStatusLine statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    public HttpResponseBuilder contentType(final String contentType) {
        headers.put(CONTENT_TYPE, contentType);
        return this;
    }

    public HttpResponseBuilder body(final HttpBody body) {
        this.body = body;
        headers.put(CONTENT_LENGTH, String.valueOf(body.getLength()));
        return this;
    }

    public HttpResponse build() {
        if (statusLine == null) {
            throw new IllegalStateException("statusLine not set");
        }
        if (headers == null) {
            headers = new HttpHeaders();
        }
        return new HttpResponse(statusLine, headers, body);
    }
}

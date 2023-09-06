package org.apache.coyote.http.response;

import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeaders;

public class HttpResponseBuilder {

    private StatusLine statusLine;
    private HttpHeaders httpHeaders;
    private HttpBody body;

    HttpResponseBuilder() {
    }

    public HttpResponseBuilder statusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    public HttpResponseBuilder httpHeaders(final HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
        return this;
    }

    public HttpResponseBuilder body(final HttpBody body) {
        this.body = body;
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(statusLine, httpHeaders, body);
    }
}

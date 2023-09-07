package org.apache.coyote.http.response;

import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeader;
import org.apache.coyote.http.common.HttpHeaders;

import java.util.EnumMap;

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

    public HttpResponseBuilder httpHeaders(final HttpHeader key, final String value) {
        if (httpHeaders == null) {
            this.httpHeaders = new HttpHeaders(new EnumMap<>(HttpHeader.class));
        }

        httpHeaders.add(key, value);
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

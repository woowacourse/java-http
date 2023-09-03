package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.header.HttpHeadersLine;
import org.apache.coyote.http11.request.body.HttpBodyLine;
import org.apache.coyote.http11.request.start.HttpStartLine;

public class HttpRequest {
    private final HttpStartLine httpStartLine;
    private final HttpHeadersLine httpHeadersLine;
    private final HttpBodyLine httpBodyLine;

    public HttpRequest(final HttpStartLine httpStartLine, final HttpHeadersLine httpHeadersLine, final HttpBodyLine httpBodyLine) {
        this.httpStartLine = httpStartLine;
        this.httpHeadersLine = httpHeadersLine;
        this.httpBodyLine = httpBodyLine;
    }

    public HttpStartLine getHttpStartLine() {
        return httpStartLine;
    }

}

package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final HttpStartLine httpStartLine;
    private final HttpRequestHeaders httpRequestHeaders;

    public HttpRequest(final HttpStartLine httpStartLine, final HttpRequestHeaders httpRequestHeaders) {
        this.httpStartLine = httpStartLine;
        this.httpRequestHeaders = httpRequestHeaders;
    }

    public static HttpRequest of(final String startLine, final Map<String, String> headers) {
        return new HttpRequest(
                HttpStartLine.from(startLine),
                HttpRequestHeaders.from(headers)
        );
    }

    public HttpStartLine getHttpStartLine() {
        return httpStartLine;
    }

    public HttpRequestHeaders getHttpRequestHeaders() {
        return httpRequestHeaders;
    }
}

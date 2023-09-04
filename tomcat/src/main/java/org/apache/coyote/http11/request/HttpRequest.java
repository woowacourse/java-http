package org.apache.coyote.http11.request;

import java.util.List;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader header;

    private HttpRequest(final HttpRequestStartLine startLine, final HttpRequestHeader header) {
        this.startLine = startLine;
        this.header = header;
    }

    public static HttpRequest of(final String startLine, final List<String> headers) {
        HttpRequestStartLine requestStartLine = HttpRequestStartLine.from(startLine);
        HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(headers);

        return new HttpRequest(requestStartLine, httpRequestHeader);
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public HttpRequestHeader getHeader() {
        return header;
    }
}

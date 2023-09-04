package org.apache.coyote.http11.request;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    private HttpRequest(final HttpRequestStartLine startLine, final HttpRequestHeader header,
                        final HttpRequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest of(final HttpRequestStartLine startLine, final HttpRequestHeader header,
                                 final HttpRequestBody body) {
        return new HttpRequest(startLine, header, body);
    }

    public static HttpRequest of(final HttpRequestStartLine startLine, final HttpRequestHeader header) {
        return new HttpRequest(startLine, header, null);
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public HttpRequestHeader getHeader() {
        return header;
    }

    public HttpRequestBody getBody() {
        return body;
    }
}

package org.apache.coyote.http11.request;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final HttpRequestHeaders header;
    private final HttpRequestBody body;

    private HttpRequest(final HttpRequestStartLine startLine, final HttpRequestHeaders header,
                        final HttpRequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest of(final HttpRequestStartLine startLine, final HttpRequestHeaders header,
                                 final HttpRequestBody body) {
        return new HttpRequest(startLine, header, body);
    }

    public static HttpRequest of(final HttpRequestStartLine startLine, final HttpRequestHeaders header) {
        return new HttpRequest(startLine, header, null);
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public HttpRequestBody getBody() {
        return body;
    }

    public String getHttpVersion() {
        return startLine.getHttpVersion();
    }

    public HttpRequestMethod getMethod() {
        return startLine.getHttpMethod();
    }

    public HttpRequestUri getUri() {
        return startLine.getHttpRequestUri();
    }
}

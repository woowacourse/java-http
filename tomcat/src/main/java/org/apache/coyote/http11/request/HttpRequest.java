package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {

    private final HttpRequestStartLine httpRequestStartLine;
    private final HttpRequestHeaders httpRequestHeaders;

    public HttpRequest(final HttpRequestStartLine httpRequestStartLine, final HttpRequestHeaders httpRequestHeaders) {
        this.httpRequestStartLine = httpRequestStartLine;
        this.httpRequestHeaders = httpRequestHeaders;
    }

    public static HttpRequest of(final String startLine, final Map<String, String> headers) {
        return new HttpRequest(
                HttpRequestStartLine.from(startLine),
                HttpRequestHeaders.from(headers)
        );
    }

    public HttpRequestStartLine getHttpStartLine() {
        return httpRequestStartLine;
    }

    public String getHeader(final String header) {
        return httpRequestHeaders.get(header);
    }

    public String getParam(final String parameter) {
        return httpRequestStartLine.getParam(parameter);
    }
}

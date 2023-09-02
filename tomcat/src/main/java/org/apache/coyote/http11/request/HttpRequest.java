package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {

    private final HttpRequestStartLine httpRequestStartLine;
    private final Map<String, String> httpRequestHeaders;
    private final Map<String, String> queryParams;

    public HttpRequest(
            final HttpRequestStartLine httpRequestStartLine,
            final Map<String, String> httpRequestHeaders,
            final Map<String, String> queryParams) {
        this.httpRequestStartLine = httpRequestStartLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.queryParams = queryParams;
    }

    public static HttpRequest of(final HttpRequestStartLine httpRequestStartLine, final Map<String, String> headers) {
        return new HttpRequest(
                httpRequestStartLine,
                headers,
                httpRequestStartLine.getQueryParams()
        );
    }

    public HttpRequestStartLine getHttpStartLine() {
        return httpRequestStartLine;
    }

    public String getHeader(final String header) {
        return httpRequestHeaders.get(header);
    }

    public String getParam(final String parameter) {
        return queryParams.get(parameter);
    }
}

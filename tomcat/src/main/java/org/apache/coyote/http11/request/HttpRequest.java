package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {

    private final HttpRequestStartLine httpRequestStartLine;
    private final Map<String, String> httpRequestHeaders;
    private final Map<String, String> queryParams;
    private final Map<String, String> payload;

    public HttpRequest(
            final HttpRequestStartLine httpRequestStartLine,
            final Map<String, String> httpRequestHeaders,
            final Map<String, String> queryParams,
            final Map<String, String> payload) {
        this.httpRequestStartLine = httpRequestStartLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.queryParams = queryParams;
        this.payload = payload;
    }

    public static HttpRequest of(final HttpRequestStartLine httpRequestStartLine, final Map<String, String> headers) {
        return new HttpRequest(
                httpRequestStartLine,
                headers,
                httpRequestStartLine.getQueryParams(),
                Map.of()
        );
    }

    public static HttpRequest of(
            final HttpRequestStartLine httpRequestStartLine,
            final Map<String, String> headers,
            final Map<String, String> payload
    ) {
        return new HttpRequest(
                httpRequestStartLine,
                headers,
                httpRequestStartLine.getQueryParams(),
                payload
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

    public String getPayloadValue(final String key) {
        return payload.get(key);
    }
}

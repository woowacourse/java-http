package org.apache.coyote.http11;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpHeaders headers;
    private final HttpQueryParams httpQueryParams;
    private final String body;

    public HttpRequest(
            HttpRequestLine httpRequestLine,
            HttpHeaders headers,
            HttpQueryParams httpQueryParams,
            String body
    ) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        this.httpQueryParams = httpQueryParams;
        this.body = body;
    }

    public HttpRequestLine getRequestLine() {
        return httpRequestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpQueryParams getQueryParams() {
        return httpQueryParams;
    }

    public String getBody() {
        return body;
    }
}

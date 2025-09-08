package org.apache.coyote.http11.httprequest;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody httpRequestBody;

    public HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders,
                       final RequestBody httpRequestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.httpRequestBody = httpRequestBody;
    }

    public HttpMethod getHttpMethod() {
        return this.requestLine.getHttpMethod();
    }

    public boolean isPathEqualsTo(final String requestPath) {
        return this.requestLine.isPathEqualsTo(requestPath);
    }

    public String getBodyParameter(final String key) {
        return this.httpRequestBody.getParameter(key);
    }

    public String getStaticResourcePath() {
        return this.requestLine.getRequestPath();
    }
}

package org.apache.coyote.http11.request;

public class HttpRequest {

    private final HttpMethod httpMethod;

    private final HttpRequestPath httpRequestPath;

    private final HttpHeaders httpHeaders;

    public HttpRequest(HttpMethod httpMethod, HttpRequestPath httpRequestPath, HttpHeaders httpHeaders) {
        this.httpMethod = httpMethod;
        this.httpRequestPath = httpRequestPath;
        this.httpHeaders = httpHeaders;
    }

    public String getHttpRequestPath() {
        return httpRequestPath.uri();
    }

    public String getContentType() {
        return httpHeaders.getContentType();
    }
}

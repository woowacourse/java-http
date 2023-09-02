package org.apache.coyote.common;

public class HttpRequest {

    private final RequestUri requestUri;
    private final HttpHeaders headers;

    public HttpRequest(RequestUri requestUri, HttpHeaders headers) {
        this.requestUri = requestUri;
        this.headers = headers;
    }

    public String getPath() {
        return requestUri.getPath();
    }

    public HttpMethod getHttpMethod() {
        return requestUri.getHttpMethod();
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}

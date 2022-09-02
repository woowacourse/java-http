package org.apache.coyote.http11.request;

public class HttpRequest {

    private final HttpMethod method;
    private final String requestURI;

    public HttpRequest(HttpMethod method, String url) {
        this.method = method;
        this.requestURI = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }
}

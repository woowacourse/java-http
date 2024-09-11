package org.apache.coyote;

public class HttpRequestStartLine {

    private final HttpMethod httpMethod;
    private final String requestURI;

    public HttpRequestStartLine(HttpMethod httpMethod, String requestURI) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }
}

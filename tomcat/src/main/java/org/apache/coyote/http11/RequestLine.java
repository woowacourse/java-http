package org.apache.coyote.http11;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final RequestURI requestURI;
    private final String httpVersion;

    public RequestLine(final HttpMethod httpMethod, final String requestURI, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestURI = new RequestURI(requestURI);
        this.httpVersion = httpVersion;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestURI getRequestURI() {
        return requestURI;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}

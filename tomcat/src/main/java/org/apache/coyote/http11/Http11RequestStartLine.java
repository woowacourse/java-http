package org.apache.coyote.http11;

public class Http11RequestStartLine {

    private final Http11Method httpMethod;
    private final String requestURI;

    public Http11RequestStartLine(Http11Method httpMethod, String requestURI) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
    }

    public Http11Method getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }
}

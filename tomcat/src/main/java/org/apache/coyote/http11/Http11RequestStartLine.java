package org.apache.coyote.http11;

public class Http11RequestStartLine {

    private static final String HTTP11_VERSION = "HTTP/1.1";

    private final String httpVersion;
    private final Http11Method httpMethod;
    private final String requestURI;

    public Http11RequestStartLine(String httpVersion, Http11Method httpMethod, String requestURI) {
        this.httpVersion = httpVersion;
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        validate();
    }

    private void validate() {
        if (!HTTP11_VERSION.equals(httpVersion)) {
            throw new IllegalArgumentException("해당 Http version 은 지원하지 않습니다: " + httpVersion);
        }
    }

    public Http11Method getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }
}

package org.apache.coyote.http11;

public class RequestLine {

    private final String httpMethod;
    private final String requestTarget;
    private final String httpVersion;

    public RequestLine(final String httpMethod, final String requestTarget, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}

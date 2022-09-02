package org.apache.coyote.http11;

public class RequestLine {

    private final String httpMethod;
    private final RequestTarget requestTarget;
    private final String httpVersion;

    public RequestLine(final String httpMethod, final String requestTarget, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestTarget = new RequestTarget(requestTarget);
        this.httpVersion = httpVersion;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public RequestTarget getRequestTarget() {
        return requestTarget;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}

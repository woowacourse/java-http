package org.apache.coyote.http11;

public class RequestLine {

    private final HttpMethod method;
    private final String requestUri;
    private final String httpVersion;

    public RequestLine(HttpMethod method, String requestUri, String httpVersion) {
        this.method = method;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }
}

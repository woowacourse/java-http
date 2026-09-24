package org.apache.coyote.http11;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String requestBody;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public RequestLine line() {
        return requestLine;
    }

    public String headerValueOf(final String headerKey) {
        return headers.valueOf(headerKey);
    }

    public String requestBody() {
        return requestBody;
    }
}

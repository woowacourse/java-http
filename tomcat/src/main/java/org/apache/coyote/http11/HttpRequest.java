package org.apache.coyote.http11;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String requestBody;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders headers,
        final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public HttpMethod method() {
        return requestLine.method();
    }

    public String path() {
        return requestLine.path();
    }

    public RequestLine line() {
        return requestLine;
    }

    public String headerValueOf(final String key) {
        return headers.valueOf(key);
    }

    public HttpCookie cookie() {
        return HttpCookie.from(headers.valueOf("Cookie"));
    }

    public String requestBody() {
        return requestBody;
    }
}

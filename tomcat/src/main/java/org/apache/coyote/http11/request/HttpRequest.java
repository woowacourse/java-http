package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final Map<String, String> body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final Map<String, String> body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final RequestLine requestLine, final HttpHeaders header, final Map<String, String> body) {
        return new HttpRequest(requestLine, header, body);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getTarget() {
        return requestLine.getTarget();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public Map<String, String> getHeaders() {
        // TODO: refactoring
        return headers.getHeaders();
    }

    public Map<String, String> getBody() {
        return body;
    }
}

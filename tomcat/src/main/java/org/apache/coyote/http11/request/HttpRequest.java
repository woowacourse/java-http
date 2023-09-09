package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final String body;

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, String body) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public List<String> header(String headerName) {
        return httpHeaders.get(headerName);
    }

    public Optional<String> sessionId() {
        return httpHeaders.sessionId();
    }

    public String method() {
        return requestLine.getHttpMethod();
    }

    public String path() {
        return requestLine.getPath();
    }

    public String httpVersion() {
        return requestLine.getHttpVersion();
    }

    public String getBody() {
        return body;
    }
}

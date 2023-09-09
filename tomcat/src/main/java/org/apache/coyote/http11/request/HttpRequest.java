package org.apache.coyote.http11.request;

import java.util.List;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;

    private HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
    }

    public static HttpRequest from(List<String> lines) {
        RequestLine requestLine = RequestLine.from(lines.get(0));
        HttpHeaders httpHeaders = HttpHeaders.from(lines.subList(1, lines.size()));
        return new HttpRequest(requestLine, httpHeaders);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public List<String> header(String headerName) {
        return httpHeaders.get(headerName);
    }

    public String method() {
        return requestLine.getHttpMethod();
    }

    public String uri() {
        return requestLine.getRequestUri();
    }

    public String httpVersion() {
        return requestLine.getHttpVersion();
    }
}

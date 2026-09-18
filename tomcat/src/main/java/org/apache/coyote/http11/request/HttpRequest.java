package org.apache.coyote.http11.request;

import java.util.Set;

public class HttpRequest {
    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, HttpBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(String requestLine, HttpHeaders headers, HttpBody body) {
        return new HttpRequest(RequestLine.from(requestLine), headers, body);
    }

    public static HttpRequest from(
            String requestLine,
            HttpHeaders headers,
            HttpBody body,
            Set<HttpMethod> supportedMethods
    ) {
        RequestLine parsedRequestLine = RequestLine.from(requestLine, supportedMethods);

        return new HttpRequest(parsedRequestLine, headers, body);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getHttpPath() {
        return requestLine.getPath();
    }

    public String getParams(String key) {
        return requestLine.getParams(key);
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public HttpBody getBody() {
        return body;
    }
}

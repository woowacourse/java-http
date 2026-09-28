package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {
    private RequestLine requestLine;
    private Map<String, String> headers;
    private Map<String, String> body;

    public HttpRequest(RequestLine requestLine, Map<String, String> headers, Map<String, String> body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = Map.copyOf(body);
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public boolean isMethod(HttpMethod method) {
        return requestLine.method() == method;
    }

    public boolean matchesPath(String path) {
        return requestLine.matchPath(path);
    }

    public String getBodyValue(String name) {
        return body.getOrDefault(name, null);
    }

    public String path() {
        return requestLine.path();
    }
}

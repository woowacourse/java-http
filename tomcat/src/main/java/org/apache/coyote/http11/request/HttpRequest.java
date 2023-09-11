package org.apache.coyote.http11.request;

import org.apache.coyote.http11.types.HttpMethod;
import org.apache.coyote.http11.types.HttpProtocol;

import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final String body;
    private final Map<String, String> headers;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.body = body;
        this.headers = headers;
    }

    public static HttpRequest of(RequestLine requestLine, Map<String, String> headers, String body) {
        return new HttpRequest(requestLine, headers, body);
    }

    public String getPath() {
        return this.requestLine.getPath();
    }

    public HttpMethod getMethod() {
        return this.requestLine.getMethod();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    public HttpProtocol getProtocol() {
        return this.requestLine.getProtocol();
    }
}

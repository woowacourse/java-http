package org.apache.coyote.http11.request;

import org.apache.coyote.http11.types.HttpMethod;
import org.apache.coyote.http11.types.HttpProtocol;

import java.util.Map;

public class HttpRequest {

    private final String path;
    private final HttpMethod method;
    private final HttpProtocol protocol;
    private final String body;
    private final Map<String, String> headers;

    private HttpRequest(String path, HttpMethod method, HttpProtocol protocol, Map<String, String> headers, String body) {
        this.path = path;
        this.method = method;
        this.protocol = protocol;
        this.body = body;
        this.headers = headers;
    }

    public static HttpRequest of(RequestLine requestLine, Map<String, String> headers, String body) {
        String path = requestLine.getPath();
        HttpMethod method = requestLine.getMethod();
        HttpProtocol protocol = requestLine.getProtocol();
        return new HttpRequest(path, method, protocol, headers, body);
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }
}

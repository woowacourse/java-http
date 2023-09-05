package org.apache.coyote.http11.message.request;


import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpVersion;

public class HttpRequest {

    private final HttpMethod method;
    private final RequestURI requestURI;
    private final HttpVersion httpVersion;
    private final Headers headers;
    private final RequestBody requestBody;

    public HttpRequest(HttpMethod method, RequestURI requestURI, HttpVersion httpVersion,
                        Headers headers, RequestBody requestBody) {
        this.method = method;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public RequestURI getRequestURI() {
        return requestURI;
    }

    public Headers getHeaders() {
        return headers;
    }

    public RequestBody getBody() {
        return requestBody;
    }
}

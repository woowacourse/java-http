package org.apache.coyote.http11.request;

import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.request.spec.HttpHeaders;
import org.apache.coyote.http11.request.spec.HttpMethod;
import org.apache.coyote.http11.request.spec.StartLine;

public class HttpRequest {

    private final StartLine startLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(StartLine startLine, HttpHeaders headers) {
        this(startLine, headers, null);
    }

    public HttpRequest(StartLine startLine, HttpHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public boolean isPathEqualTo(String path) {
        return startLine.isPathEqualTo(path);
    }

    public boolean isStaticResourcePath() {
        return startLine.isStaticResourcePath();
    }

    public boolean hasCookie() {
        return headers.hasCookie();
    }

    public String getPathString() {
        return startLine.getPath();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Cookie getCookie() {
        return headers.getCookie();
    }
}

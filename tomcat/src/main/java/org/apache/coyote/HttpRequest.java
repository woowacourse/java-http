package org.apache.coyote;

import java.util.Map;

public class HttpRequest {

    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader header;
    private final HttpCookies cookie;
    private final HttpRequestBody body;

    public HttpRequest(HttpRequestStartLine startLine,
                       HttpRequestHeader header,
                       HttpCookies cookie,
                       HttpRequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.cookie = cookie;
        this.body = body;
    }

    public HttpMethod getHttpMethod() {
        return startLine.getHttpMethod();
    }

    public String getRequestURI() {
        return startLine.getRequestURI();
    }

    public HttpRequestHeader getHeader() {
        return header;
    }

    public HttpCookies getCookie() {
        return cookie;
    }

    public Map<String, String> getBody() {
        return body.getBody();
    }
}

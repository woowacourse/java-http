package org.apache.coyote;

import java.util.Map;

public class HttpRequest {

    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    public HttpRequest(HttpRequestStartLine startLine, HttpRequestHeader header, HttpRequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public HttpMethod getHttpMethod() {
        return startLine.getHttpMethod();
    }

    public String getRequestURI() {
        return startLine.getRequestURI();
    }

    public Map<String, String> getBody() {
        return body.getBody();
    }
}

package org.apache.coyote.servlet.request;

import org.apache.coyote.support.HttpMethod;

public class HttpRequest {

    private final StartLine startLine;
    private final RequestHeaders headers;
    private final String body;

    public HttpRequest(StartLine startLine, RequestHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public boolean isMethodOf(HttpMethod method) {
        return startLine.hasMethodOf(method);
    }

    public String getUri() {
        return startLine.getUri();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public Parameters getParameters() {
        if (headers.hasParametersAsBody()) {
            return Parameters.of(body);
        }
        return startLine.getParameters();
    }
}

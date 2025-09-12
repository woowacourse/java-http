package org.apache.coyote.http11.request;

import java.util.Map;

public class Http11Request {

    private final StartLine startLine;
    private final Http11RequestHeaders headers;
    private final Http11RequestBody body;

    public Http11Request(
            final StartLine startLine,
            final Http11RequestHeaders headers,
            final Http11RequestBody body
    ) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public String getSessionId() {
        return headers.getSessionId();
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return startLine.isSameHttpMethod(httpMethod);
    }

    public Map<String, String> extractRequestBodyParams() {
        return body.parseFormParams();
    }

    public String getUri() {
        return startLine.getUri();
    }

    public HttpMethod getHttpMethod() {
        return startLine.getHttpMethod();
    }
}

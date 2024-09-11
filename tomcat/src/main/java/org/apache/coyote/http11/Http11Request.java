package org.apache.coyote.http11;

import java.util.Map;

public class Http11Request {

    private final Http11RequestStartLine startLine;
    private final Http11RequestHeader header;
    private final Http11RequestBody body;

    public Http11Request(Http11RequestStartLine startLine, Http11RequestHeader header, Http11RequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public Http11Method getHttpMethod() {
        return startLine.getHttpMethod();
    }

    public String getRequestURI() {
        return startLine.getRequestURI();
    }

    public Map<String, String> getBody() {
        return body.getBody();
    }
}

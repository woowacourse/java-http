package org.apache.coyote.http11;

import java.util.Map;

public class Http11Request {

    private final Http11RequestStartLine startLine;

    public Http11Request(Http11RequestStartLine startLine) {
        this.startLine = startLine;
    }

    public Http11Method getHttpMethod() {
        return startLine.getHttpMethod();
    }

    public String getRequestURI() {
        return startLine.getRequestURI();
    }

    public Map<String, String> getQueryParameters() {
        return startLine.getQueryParameters();
    }
}

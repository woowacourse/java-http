package org.apache.coyote.http11;

import java.util.StringTokenizer;

public class HttpRequest {
    private final String method;
    private final String pathInfo;
    private final String protocol;

    public HttpRequest(String requestLine) {
        StringTokenizer tokenizer = new StringTokenizer(requestLine);
        method = tokenizer.nextToken();
        pathInfo = tokenizer.nextToken();
        protocol = tokenizer.nextToken();
    }

    public String getMethod() {
        return method;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getProtocol() {
        return protocol;
    }
}

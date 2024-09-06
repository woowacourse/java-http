package org.apache.catalina.request;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> payload;

    public HttpRequest(String method, String path, Map<String, String> headers, Map<String, String> payload) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.payload = payload;
    }
}

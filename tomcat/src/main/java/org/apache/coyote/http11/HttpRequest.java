package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryString;
    private final String protocolVersion;

    public HttpRequest(String method, String path, Map<String, String> queryString, String protocolVersion) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.protocolVersion = protocolVersion;
    }

    public String getPath() {
        return this.path;
    }
}

package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryString;
    private final String protocolVersion;
    private final Map<String, String> headers;

    public HttpRequest(String method, String path, Map<String, String> queryString, String protocolVersion,
                       Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
    }

    public String getQueryStringData(String input) {
        return queryString.get(input);
    }

    public String getPath() {
        return this.path;
    }

    public String getMethod() {
        return this.method;
    }
}

package org.apache.coyote.util.request;

import java.util.Map;

public class RequestLine {

    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> queryParams;

    public RequestLine(String method, String path, String version, Map<String, String> queryParams) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.queryParams = queryParams;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}

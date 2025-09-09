package org.apache.coyote.http11.message.request;

import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final String version;

    public HttpRequest(String method, String path, Map<String, String> queryParams, String version) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getVersion() {
        return version;
    }
}

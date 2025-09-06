package org.apache.coyote.http11.message.request;

import java.util.Collections;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> queryParams;

    public HttpRequest(String method, String path, Map<String, String> queryParams) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams != null ? queryParams : Collections.emptyMap();
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
}

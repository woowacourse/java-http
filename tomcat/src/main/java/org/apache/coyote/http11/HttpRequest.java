package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final HttpRequestMethod method;
    private final String path;
    private final Map<String, String> queryParams;

    public HttpRequest(HttpRequestMethod method, String path, Map<String, String> queryParams) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }

    public boolean hasMethod(HttpRequestMethod method) {
        return this.method == method;
    }

    public boolean isPath(String path) {
        return this.path.equals(path);
    }

    public boolean endsWith(String type) {
        return path.endsWith(type);
    }
}

package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final String path;
    private final Map<String, String> queryParams;

    public HttpRequest(String path, Map<String, String> queryParams) {
       this.path = path;
       this.queryParams = queryParams;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }
}

package org.apache.coyote.http11.httpRequest;

import java.util.Map;

public class HttpRequest {

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> queryStrings;

    public HttpRequest(HttpMethod method, String path, Map<String, String> queryStrings) {
        this.method = method;
        this.path = path;
        this.queryStrings = queryStrings;
    }

    public boolean pathEquals(String path) {
        return this.path.equals(path);
    }

    public String getQueryStringOf(String key) {
        return queryStrings.get(key);
    }

    public String getPath() {
        return path;
    }
}

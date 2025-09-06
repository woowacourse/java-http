package org.apache.coyote.http11.httpRequest;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryStrings;

    public HttpRequest(String method, String path, Map<String, String> queryStrings) {
        this.method = method;
        this.path = path;
        this.queryStrings = queryStrings;
    }

    public boolean pathStartsWith(String prefix) {
        return path.startsWith(prefix);
    }

    public boolean pathEquals(String path) {
        return this.path.equals(path);
    }

    public boolean isStaticFileRequest() {
        return path.lastIndexOf(".") != -1;
    }

    public String getQueryStringOf(String key) {
        return queryStrings.get(key);
    }

    public String getPath() {
        return path;
    }
}

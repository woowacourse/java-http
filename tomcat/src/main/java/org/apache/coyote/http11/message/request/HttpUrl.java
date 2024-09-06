package org.apache.coyote.http11.message.request;

import java.util.List;
import java.util.Map;

public class HttpUrl {
    private final String path;
    private final Map<String, List<String>> queryParameters;

    public HttpUrl(String path, Map<String, List<String>> queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public String getPath() {
        return path;
    }

    public Map<String, List<String>> getQueryParameters() {
        return queryParameters;
    }
}

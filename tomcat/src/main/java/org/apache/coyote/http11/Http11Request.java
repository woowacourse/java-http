package org.apache.coyote.http11;

import java.util.Collections;
import java.util.Map;

public class Http11Request {

    private final String uri;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;

    public Http11Request(String uri, Map<String, String> queryParams, Map<String, String> headers) {
        this.uri = uri;
        this.queryParams = queryParams;
        this.headers = headers;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return uri.split("\\?")[0];
    }

    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}

package org.apache.coyote.common;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String protocol;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String body;

    public HttpRequest(final String method, final String path, final String protocol, final Map<String, String> headers,
                       final Map<String, String> queryParams, final String body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(final String key) {
        if (!headers.containsKey(key)) {
            throw new IllegalArgumentException("존재하지 않는 헤더입니다.");
        }
        return headers.get(key);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getBody() {
        return body;
    }
}

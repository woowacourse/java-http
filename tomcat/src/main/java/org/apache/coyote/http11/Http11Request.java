package org.apache.coyote.http11;

import java.util.Collections;
import java.util.Map;

public final class Http11Request {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final String body;

    public Http11Request(
            final String method,
            final String path,
            final Map<String, String> queryParams,
            final Map<String, String> headers,
            final String body
    ) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
        this.body = body;
    }

    public static Http11Request createInvalid() {
        return new Http11Request("", "/", Collections.emptyMap(), Collections.emptyMap(), "");
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getBody() {
        return body;
    }
}

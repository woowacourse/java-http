package org.apache.coyote.http11;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class RequestTarget {

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private String requestBody;

    public RequestTarget(final HttpMethod httpMethod, final String path,
        final Map<String, String> queryParams, final Map<String, String> headers,
        final String requestBody) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

    public String path() {
        return path;
    }

    public Map<String, String> queryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public String requestBody() {
        return requestBody;
    }
}

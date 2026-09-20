package org.apache.coyote.http11;

import java.util.Map;

public class Request {

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> headers;
    private final String requestBody;

    public Request(final HttpMethod httpMethod, final String path,
        final Map<String, String> headers,
        final String requestBody) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public String headerValue(final String key) {
        return headers.get(key);
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

    public String path() {
        return path;
    }

    public String requestBody() {
        return requestBody;
    }
}

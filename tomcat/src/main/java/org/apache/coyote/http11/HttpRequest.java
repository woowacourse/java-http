package org.apache.coyote.http11;

import java.util.Map;

public final class HttpRequest {

    private final String method;
    private final String requestTarget;
    private final String path;
    private final String queryString;
    private final String protocol;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(final String method, final String requestTarget, final String protocol,
                       final Map<String, String> headers, final String body) {
        this.method = method;
        this.requestTarget = requestTarget;
        this.protocol = protocol;
        this.headers = Map.copyOf(headers);
        this.body = body;
        final int queryIndex = requestTarget.indexOf('?');
        if (queryIndex >= 0) {
            this.path = requestTarget.substring(0, queryIndex);
            this.queryString = requestTarget.substring(queryIndex + 1);
            return;
        }
        this.path = requestTarget;
        this.queryString = "";
    }

    public String getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getRequestLine() {
        return String.join(" ", method, requestTarget, protocol);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(final String name) {
        return headers.get(name.toLowerCase());
    }

    public String getBody() {
        return body;
    }
}

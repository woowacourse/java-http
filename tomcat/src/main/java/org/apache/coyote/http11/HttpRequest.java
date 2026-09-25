package org.apache.coyote.http11;

import java.util.Map;
import java.util.Map;

public final class HttpRequest {

    private final String method;              // GET, POST
    private final String requestTarget;       // /login?account=gugu
    private final String path;                // /login
    private final String queryString;         // account=gugu
    private final String protocol;            // HTTP/1.1
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(final String method, final String requestTarget,
                       final Map<String, String> headers, final String body) {
        this(method, requestTarget, "HTTP/1.1", headers, body);
    }

    public HttpRequest(final String method, final String requestTarget, final String protocol,
                       final Map<String, String> headers, final String body) {
        this.method = method;
        this.requestTarget = requestTarget;
        this.protocol = protocol;
        final int queryIndex = requestTarget.indexOf('?');
        this.path = queryIndex >= 0 ? requestTarget.substring(0, queryIndex) : requestTarget;
        this.queryString = queryIndex >= 0 ? requestTarget.substring(queryIndex + 1) : "";
        this.headers = Map.copyOf(headers);
        this.body = body;
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

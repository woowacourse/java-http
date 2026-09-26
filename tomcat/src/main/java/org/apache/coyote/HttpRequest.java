package org.apache.coyote;

import java.util.Map;

public final class HttpRequest {

    private static final String COOKIE = "Cookie";
    private static final String CONTENT_TYPE = "Content-Type";

    private final String method;
    private final String target;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(
            String method,
            String target,
            String version,
            Map<String, String> headers,
            String body
    ) {
        this.method = method;
        this.target = target;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }

    public String getBody() {
        return body;
    }

    public String getCookie() {
        return headers.get(COOKIE);
    }

    public String getContentType() {
        return headers.get(CONTENT_TYPE);
    }
}

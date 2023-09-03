package org.apache.coyote.domain;

import java.util.Map;

public class HttpRequestHeader {

    private final String method;
    private final String uri;
    private final String version;
    private final Map<String, String> headers;

    public HttpRequestHeader(final String method, final String uri, final String version, final Map<String, String> headers) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.headers = headers;

    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
